package com.voice.v2.auth;


import com.voice.v2.model.auth.UserAuth;
import com.voice.v2.service.auth.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.converter.ClaimConversionService;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * An implementation of an {@link OAuth2UserService} that supports OpenID Connect 1.0
 * Provider's.
 *
 * @author Joe Grandja
 * @see OAuth2UserService
 * @see OidcUserRequest
 * @see OidcUser
 * @see DefaultOidcUser
 * @see OidcUserInfo
 * @since 5.0
 */
@Component
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
    private static final String USER_AUTH_CLAIM_KEY = "UserAuth";
    private static final String INVALID_USER = "invalid_user";
    private static final Converter<Map<String, Object>, Map<String, Object>> DEFAULT_CLAIM_TYPE_CONVERTER = new ClaimTypeConverter(
            createDefaultClaimTypeConverters());
    @Autowired
    private UserAuthService userAuthService;
    private Set<String> accessibleScopes = new HashSet<>(
            Arrays.asList(OidcScopes.PROFILE, OidcScopes.EMAIL, OidcScopes.ADDRESS, OidcScopes.PHONE));

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();

    private Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory = (
            clientRegistration) -> DEFAULT_CLAIM_TYPE_CONVERTER;

    /**
     * Returns the default {@link Converter}'s used for type conversion of claim values
     * for an {@link OidcUserInfo}.
     *
     * @return a {@link Map} of {@link Converter}'s keyed by {@link StandardClaimNames
     * claim name}
     * @since 5.2
     */
    public static Map<String, Converter<Object, ?>> createDefaultClaimTypeConverters() {
        Converter<Object, ?> booleanConverter = getConverter(TypeDescriptor.valueOf(Boolean.class));
        Converter<Object, ?> instantConverter = getConverter(TypeDescriptor.valueOf(Instant.class));
        Map<String, Converter<Object, ?>> claimTypeConverters = new HashMap<>();
        claimTypeConverters.put(StandardClaimNames.EMAIL_VERIFIED, booleanConverter);
        claimTypeConverters.put(StandardClaimNames.PHONE_NUMBER_VERIFIED, booleanConverter);
        claimTypeConverters.put(StandardClaimNames.UPDATED_AT, instantConverter);
        return claimTypeConverters;
    }

    private static Converter<Object, ?> getConverter(TypeDescriptor targetDescriptor) {
        TypeDescriptor sourceDescriptor = TypeDescriptor.valueOf(Object.class);
        return (source) -> ClaimConversionService.getSharedInstance()
                .convert(source, sourceDescriptor, targetDescriptor);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        Optional<UserAuth> userAuth = Optional.empty();
        Assert.notNull(userRequest, "userRequest cannot be null");
        OidcUserInfo userInfo = null;
        if (userRequest.getIdToken() != null && !userRequest.getIdToken().getEmail().isEmpty()) {
            userAuth = userAuthService.getUserAuthByEmail(userRequest.getIdToken().getEmail());
            if (userAuth.isEmpty()) {
                OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER);
                throw new OAuth2AuthenticationException(oauth2Error, "User Not Allowed " + userRequest.getIdToken().getEmail());
            }
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("UserAuth",userAuth);
        userInfo = new OidcUserInfo(claims);

        //Here we need to add roles, permissions and principal
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        userAuth.ifPresent(test -> authorities.add(new SimpleGrantedAuthority(test.getUserRole().name())));
        authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String authority : token.getScopes()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
        }
        return getUser(userRequest, userInfo, authorities);
    }

    private Map<String, Object> getClaims(OidcUserRequest userRequest, OAuth2User oauth2User) {
        Converter<Map<String, Object>, Map<String, Object>> converter = this.claimTypeConverterFactory
                .apply(userRequest.getClientRegistration());
        if (converter != null) {
            return converter.convert(oauth2User.getAttributes());
        }
        return DEFAULT_CLAIM_TYPE_CONVERTER.convert(oauth2User.getAttributes());
    }

    private OidcUser getUser(OidcUserRequest userRequest, OidcUserInfo userInfo, Set<GrantedAuthority> authorities) {
        ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        }
        return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }

    private boolean shouldRetrieveUserInfo(OidcUserRequest userRequest) {
        // Auto-disabled if UserInfo Endpoint URI is not provided
        ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        if (!StringUtils.hasLength(providerDetails.getUserInfoEndpoint().getUri())) {
            return false;
        }
        // The Claims requested by the profile, email, address, and phone scope values
        // are returned from the UserInfo Endpoint (as described in Section 5.3.2),
        // when a response_type value is used that results in an Access Token being
        // issued.
        // However, when no Access Token is issued, which is the case for the
        // response_type=id_token,
        // the resulting Claims are returned in the ID Token.
        // The Authorization Code Grant Flow, which is response_type=code, results in an
        // Access Token being issued.
        if (AuthorizationGrantType.AUTHORIZATION_CODE
                .equals(userRequest.getClientRegistration().getAuthorizationGrantType())) {
            // Return true if there is at least one match between the authorized scope(s)
            // and accessible scope(s)
            //
            // Also return true if authorized scope(s) is empty, because the provider has
            // not indicated which scopes are accessible via the access token
            // @formatter:off
            return this.accessibleScopes.isEmpty()
                    || CollectionUtils.isEmpty(userRequest.getAccessToken().getScopes())
                    || CollectionUtils.containsAny(userRequest.getAccessToken().getScopes(), this.accessibleScopes);
            // @formatter:on
        }
        return false;
    }

    /**
     * Sets the {@link OAuth2UserService} used when requesting the user info resource.
     *
     * @param oauth2UserService the {@link OAuth2UserService} used when requesting the
     *                          user info resource.
     * @since 5.1
     */
    public final void setOauth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService) {
        Assert.notNull(oauth2UserService, "oauth2UserService cannot be null");
        this.oauth2UserService = oauth2UserService;
    }

    /**
     * Sets the factory that provides a {@link Converter} used for type conversion of
     * claim values for an {@link OidcUserInfo}. The default is {@link ClaimTypeConverter}
     * for all {@link ClientRegistration clients}.
     *
     * @param claimTypeConverterFactory the factory that provides a {@link Converter} used
     *                                  for type conversion of claim values for a specific {@link ClientRegistration
     *                                  client}
     * @since 5.2
     */
    public final void setClaimTypeConverterFactory(
            Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory) {
        Assert.notNull(claimTypeConverterFactory, "claimTypeConverterFactory cannot be null");
        this.claimTypeConverterFactory = claimTypeConverterFactory;
    }

    /**
     * Sets the scope(s) that allow access to the user info resource. The default is
     * {@link OidcScopes#PROFILE profile}, {@link OidcScopes#EMAIL email},
     * {@link OidcScopes#ADDRESS address} and {@link OidcScopes#PHONE phone}. The scope(s)
     * are checked against the "granted" scope(s) associated to the
     * {@link OidcUserRequest#getAccessToken() access token} to determine if the user info
     * resource is accessible or not. If there is at least one match, the user info
     * resource will be requested, otherwise it will not.
     *
     * @param accessibleScopes the scope(s) that allow access to the user info resource
     * @since 5.2
     */
    public final void setAccessibleScopes(Set<String> accessibleScopes) {
        Assert.notNull(accessibleScopes, "accessibleScopes cannot be null");
        this.accessibleScopes = accessibleScopes;
    }

}