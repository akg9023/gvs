package com.voice.auth.filters;


import com.voice.auth.model.RateLimitRule;
import com.voice.auth.service.RateLimitRuleService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Pattern "*" matching is supported only in this filter
 * Rule e.g. user.ROLE_ADMIN.GET./user.abc.def@abc.com
 * user.<ROLE>.<METHOD>.<URI>.<EMAIL>
 * Precedence Rules
 * 1. user role method uri email        -user preference
 * 2. user role method * email          -user preference for all uri
 * 3. user role method uri *            -role preference for all users
 * 4. user role method * *              -role preference for all uri and users
 * 5. global method uri                 -global preference
 * 6. global method *                   -global preference for all uri
 */
@Component
public class AfterAuthRateLimitFilter extends OncePerRequestFilter {
    /**
     * rate limit rule identifier name for URI's per user limits
     */
    public static final String RATE_LIMIT_RULE_USER = "user";
    /**
     * rate limit rule identifier name for URI's global limits
     */
    public static final String RATE_LIMIT_RULE_GLOBAL = "global";
    /**
     * Role regex expression
     */
    private static final String ROLE_REGULAR_EXPRESSION = "ROLE_.*";
    private final String EMAIL = "email";
    private final Map<String, Set<String>> patternMapCache = new HashMap<>();
    @Autowired
    RateLimitRuleService rateLimitRuleService;
    Logger logger = LoggerFactory.getLogger(AfterAuthRateLimitFilter.class);
    private boolean enforce = false;
    @Autowired
    private CustomRateLimitFilter customRateLimitFilter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.trace("AfterAuthRateLimit !!!!!!!!!!!!!!!!!!!!!!!!!!");

        Optional<Bucket> bucket = identifierBucketResolver(request);

        if (bucket.isPresent()) {
            ConsumptionProbe probe = bucket.get().tryConsumeAndReturnRemaining(1);
            if (this.enforce && !probe.isConsumed()) {

                response.sendError(429, "Too many requests");
                logger.warn("AfterAuthRateLimitFilter.maxRequestsExceeded statusCode: 429, statusMessage: Too many requests URI {} ", request.getRequestURI());

            } else if (this.enforce) {
                response.setHeader("X-Rate-Limit-Remaining", "" + probe.getRemainingTokens());
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Precedence Rules
     * 1. user role method uri email        -user preference
     * 2. user role method * email          -user preference for all uri
     * 3. user role method uri *            -role preference for all users
     * 4. user role method * *              -role preference for all uri and users
     * 5. global method uri                 -global preference
     * 6. global method *                   -global preference for all uri
     *
     * @param request HttpServletRequest
     * @return Bucket
     */
    private Optional<Bucket> identifierBucketResolver(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Optional<Bucket> patternBucket;
        String method = request.getMethod();
        String URI = request.getRequestURI();
        Principal principal = request.getUserPrincipal();

        //User Rules Check
        if (principal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {

            String email = oAuth2AuthenticationToken.getPrincipal().getAttribute(EMAIL);
            Optional<GrantedAuthority> roleAuthority = oAuth2AuthenticationToken.getAuthorities().stream().filter(authority -> Pattern.matches(ROLE_REGULAR_EXPRESSION, authority.getAuthority())).findFirst();
            String role = roleAuthority.isPresent() ? roleAuthority.get().getAuthority() : "";

            if (email != null && !email.isEmpty() && !role.isEmpty() && !method.isEmpty() && !URI.isEmpty()) {

                //Rule 1
                sb.append(RATE_LIMIT_RULE_USER).append(".").append(role).append(".").append(method).append(".").append(URI).append(".").append(email);
                patternBucket = patternBucketResolver(sb.toString());
                sb.delete(0, sb.length());
                if (patternBucket.isPresent()) {
                    return patternBucket;
                }

                //Rule 2
                sb.append(RATE_LIMIT_RULE_USER).append(".").append(role).append(".").append(method).append(".").append("*").append(".").append(email);
                patternBucket = patternBucketResolver(sb.toString());
                sb.delete(0, sb.length());
                if (patternBucket.isPresent()) {
                    return patternBucket;
                }

                //Rule 3
                sb.append(RATE_LIMIT_RULE_USER).append(".").append(role).append(".").append(method).append(".").append(URI).append(".").append("*");
                patternBucket = patternBucketResolver(sb.toString());
                sb.delete(0, sb.length());
                if (patternBucket.isPresent()) {
                    return patternBucket;
                }

                //Rule 4
                sb.append(RATE_LIMIT_RULE_USER).append(".").append(role).append(".").append(method).append(".").append("*").append(".").append("*");
                patternBucket = patternBucketResolver(sb.toString());
                sb.delete(0, sb.length());
                if (patternBucket.isPresent()) {
                    return patternBucket;
                }
            }
        }

        //Global Rules Check
        if (!method.isEmpty() && !URI.isEmpty()) {

            //Rule 5
            sb.append(RATE_LIMIT_RULE_GLOBAL).append(".").append(method).append(".").append(URI);
            patternBucket = patternBucketResolver(sb.toString());
            sb.delete(0, sb.length());
            if (patternBucket.isPresent()) {
                return patternBucket;
            }
            //Rule 6
            sb.append(RATE_LIMIT_RULE_GLOBAL).append(".").append(method).append(".").append("*");
            patternBucket = patternBucketResolver(sb.toString());
            sb.delete(0, sb.length());
            if (patternBucket.isPresent()) {
                return patternBucket;
            }
        }
        return Optional.empty();
    }

    private Optional<Bucket> patternBucketResolver(String pattern) {
        Map<String, RateLimitRule> rateLimitRuleMap = rateLimitRuleService.getRateLimitRuleMap();
        Map<String, Bucket> rateLimitBucketsMap = customRateLimitFilter.getRateLimitBucketsMap();
        if (!rateLimitRuleMap.isEmpty() && !rateLimitBucketsMap.isEmpty()) {

            if (rateLimitRuleMap.containsKey(pattern)) {
                this.enforce = rateLimitRuleMap.get(pattern).isEnforce();
                return Optional.of(rateLimitBucketsMap.get(pattern));
            }
        }
        return Optional.empty();
    }
}