package com.voice.auth.config;

//
//import com.voice.auth.filters.AfterAuthRateLimitFilter;
//import com.voice.auth.filters.CustomRateLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity//(debug = true)
@EnableMethodSecurity
public class SecurityConfig {
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @Autowired
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Autowired
    private CustomOidcUserService customOidcUserService;
//    @Autowired
//    private CustomRateLimitFilter customRateLimitFilter;
//    @Autowired
//    private AfterAuthRateLimitFilter afterAuthRateLimitFilter;

//    @Autowired
//    private ConfigUtils configurationUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //On logout to remove cookies, clear cache on the browser
        //we can also only remove cookies new ClearSiteDataHeaderWriter(Directive.COOKIES)
//        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES));

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
               .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> {
                    headers.referrerPolicy(ref ->
                            ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.UNSAFE_URL)
                    );
//                    headers.xssProtection(Customizer.withDefaults())
//                            .contentSecurityPolicy(csp -> {
//                                csp.policyDirectives(configurationUtils.getContentSeucurityPolicy());
//                            });
              })
                //This configures how many session one user can have

                .sessionManagement(session -> session
//This will prevent a user from logging in multiple times - a second login will cause the first to be invalidated.
                .maximumSessions(1)
 //The second login will then be rejected. By "rejected", we mean that the user will be sent to the authentication-failure-url
 //               .maxSessionsPreventsLogin(true)
                        )
                .authorizeHttpRequests((authorize) -> authorize
                               .requestMatchers("/v1/hlzGlobalReg/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                                .requestMatchers("yatra/admin/**").hasAuthority("ROLE_ADMIN")
                               .anyRequest().authenticated()
//                                .anyRequest().permitAll()
//                                .requestMatchers("/login").permitAll()
//                                .requestMatchers("/kafka").permitAll()
                        //.requestMatchers("/userTest").hasAuthority("ROLE_ADMIN")
//                                .requestMatchers("/ved").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
////                        .requestMatchers("/user").hasAuthority("ROLE_USER")
//                                .anyRequest().authenticated()
                )
//                .httpBasic(Customizer.withDefaults())
                .oauth2Login(oauth2 -> {
                    //oauth2.loginPage("http://localhost:3000/login");
                    oauth2.userInfoEndpoint(userInfo ->
                            userInfo.oidcUserService(customOidcUserService)
                    );
                    oauth2.successHandler(oAuth2LoginSuccessHandler);
                    oauth2.failureHandler(oAuth2LoginFailureHandler);

                });
//                .addFilterBefore(customRateLimitFilter, DisableEncodeUrlFilter.class)
//                .addFilterAfter(afterAuthRateLimitFilter, AuthorizationFilter.class);
        //.formLogin(Customizer.withDefaults());

        return httpSecurity.build();
    }

//    @Bean
//    public FilterRegistrationBean<CustomRateLimitFilter> customRateLimitFilterRegistrationBean(CustomRateLimitFilter customRateLimitFilter) {
//        FilterRegistrationBean<CustomRateLimitFilter> registrationBean = new FilterRegistrationBean<>(customRateLimitFilter);
//        registrationBean.setEnabled(false);
//        return registrationBean;
//    }
//
//    @Bean
//    public FilterRegistrationBean<AfterAuthRateLimitFilter> afterAuthRateLimitFilterRegistrationBean(AfterAuthRateLimitFilter afterAuth) {
//        FilterRegistrationBean<AfterAuthRateLimitFilter> registrationBean = new FilterRegistrationBean<>(afterAuth);
//        registrationBean.setEnabled(false);
//        return registrationBean;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new InMemoryUserDetailsManager(User.builder().passwordEncoder((pass) -> passwordEncoder().encode(pass)).username("user").password("password").authorities("ROLE_USER").build());
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://db.gaurangavedic.org.in","https://gaurangavedic.org.in","https://yatra.gaurangavedic.org.in"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}