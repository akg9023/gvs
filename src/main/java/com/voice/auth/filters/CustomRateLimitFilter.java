//package com.voice.auth.filters;
//
//
//
//import com.voice.auth.model.RateLimitRule;
//import com.voice.auth.service.RateLimitRuleService;
//import io.github.bucket4j.Bucket;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.catalina.util.TimeBucketCounter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Servlet filter that can help mitigate Denial of Service (DoS) and Brute Force attacks by limiting the number of a requests that are allowed from a single IP address within a time window (also referred to as a time bucket), e.g. 300 Requests per 60 seconds.
// * The filter works by incrementing a counter in a time bucket for each IP address, and if the counter exceeds the allowed limit then further requests from that IP are dropped with a "429 Too many requests" response until the bucket time ends and a new tokens fill bucket after the configured interval.
// * You can set enforce to false to disable the termination of requests that exceed the allowed limit. Then your application code can inspect the Request Attribute org.apache.catalina.filters.RateLimitFilter.Count and decide how to handle the request based on other information that it has, e.g. allow more requests to certain users based on roles, etc.
// * WARNING: if Tomcat is behind a reverse proxy then you must make sure that the Rate Limit Filter sees the client IP address, so if for example you are using the Remote IP Filter, then the filter mapping for the Rate Limit Filter must come after the mapping of the Remote IP Filter to ensure that each request has its IP address resolved before the Rate Limit Filter is applied. Failure to do so will count requests from different IPs in the same bucket and will result in a self inflicted DoS attack.
// */
//
///**
// * Pattern matching like "*" is not supported now. Only available in AfterAuthRateLimitFilter
// * Rule eg. global.GET./abc
// * global.<METHOD>.<URI>
// * ipaddress
// */
//@Component
//public class CustomRateLimitFilter extends OncePerRequestFilter {
//    /**
//     * default duration in seconds
//     */
//    public static final int DEFAULT_BUCKET_DURATION = 60;
//    /**
//     * default number of requests per duration
//     */
//    public static final int DEFAULT_BUCKET_REQUESTS = 300;
//    /**
//     * default value for enforce
//     */
//    public static final boolean DEFAULT_ENFORCE = true;
//    /**
//     * default status code to return if requests per duration exceeded
//     */
//    public static final int DEFAULT_STATUS_CODE = 429;
//    /**
//     * default status message to return if requests per duration exceeded
//     */
//    public static final String DEFAULT_STATUS_MESSAGE = "Too many requests";
//
//    /**
//     * rate limit rule identifier name for ip address
//     */
//    public static final String RATE_LIMIT_RULE_IP_ADDRESS = "ipaddress";
//    /**
//     * init-param to set the bucket duration in seconds
//     */
//    public static final String PARAM_BUCKET_DURATION = "bucketDuration";
//    /**
//     * init-param to set the bucket number of requests
//     */
//    public static final String PARAM_BUCKET_REQUESTS = "bucketRequests";
//    /**
//     * init-param to set the enforce flag
//     */
//    public static final String PARAM_ENFORCE = "enforce";
//    /**
//     * init-param to set a custom status code if requests per duration exceeded
//     */
//    public static final String PARAM_STATUS_CODE = "statusCode";
//    /**
//     * init-param to set a custom status message if requests per duration exceeded
//     */
//    public static final String PARAM_STATUS_MESSAGE = "statusMessage";
//    private static final long serialVersionUID = 1L;
//    Logger logger = LoggerFactory.getLogger(CustomRateLimitFilter.class);
//    transient TimeBucketCounter bucketCounter;
//    @Autowired
//    RateLimitRuleService rateLimitRuleService;
//    private int actualRequests;
//    private int bucketRequests = DEFAULT_BUCKET_REQUESTS;
//    private int bucketDuration = DEFAULT_BUCKET_DURATION;
//    private boolean enforce = DEFAULT_ENFORCE;
//    private int statusCode = DEFAULT_STATUS_CODE;
//    private String statusMessage = DEFAULT_STATUS_MESSAGE;
//    private Map<String, Bucket> rateLimitBucketsMap = new ConcurrentHashMap<>();
//
//    Map<String, Bucket> getRateLimitBucketsMap() {
//        return this.rateLimitBucketsMap;
//    }
//
//    @Override
//    public void initFilterBean() throws ServletException {
//        createBuckets();
//        logger.trace("CustomRateLimitFilter Initialized !!!!!!!!!!");
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String ipAddr = request.getRemoteAddr();
//        logger.trace("CustomRateLimitFilter1: ======================================= " + ipAddr);
//
//        if (!rateLimitBucketsMap.isEmpty() && rateLimitBucketsMap.containsKey(RATE_LIMIT_RULE_IP_ADDRESS)) {
//            RateLimitRule rateLimitRule = rateLimitRuleService.getRateLimitRuleMap().get(RATE_LIMIT_RULE_IP_ADDRESS);
//            boolean enforce = rateLimitRule != null && rateLimitRule.isEnforce();
//
//            if (enforce && !rateLimitBucketsMap.get(RATE_LIMIT_RULE_IP_ADDRESS).tryConsume(1)) {
//
//                ((HttpServletResponse) response).sendError(statusCode, statusMessage);
//                logger.warn("CustomRateLimitFilter.maxRequestsExceeded statusCode: {}, statusMessage: {}, ipAddr: {} ", statusCode, statusMessage,
//                        ipAddr);
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    public void refreshRateLimitRule(String identifierKey) {
//        if (!rateLimitRuleService.getRateLimitRuleMap().isEmpty()) {
//            RateLimitRule rateLimitRuleObj = rateLimitRuleService.getRateLimitRuleMap().get(identifierKey);
//            if (rateLimitRuleObj != null) {
//                Bucket bucket = Bucket.builder().addLimit(limit -> limit.capacity(rateLimitRuleObj.getBucketRequests()).refillIntervally(rateLimitRuleObj.getBucketRequests(), Duration.ofSeconds(rateLimitRuleObj.getBucketDuration()))).build();
//                rateLimitBucketsMap.put(identifierKey, bucket);
//                logger.trace("Rate Limit Rule Refresh Successful New Values - bucketReqeust:{} bucketDuration:{}", rateLimitRuleObj.getBucketRequests(), rateLimitRuleObj.getBucketDuration());
//            }
//        }
//    }
//
//    private void createBuckets() {
//        RateLimitRule rateLimitRule;
//        if (rateLimitRuleService != null) {
//            if (rateLimitRuleService.getRateLimitRuleMap().isEmpty())
//                rateLimitRuleService.loadAllRules();
//
//            rateLimitRuleService.getRateLimitRuleMap().forEach((key, rateLimitRuleObj) -> {
//                Bucket bucket = Bucket.builder().addLimit(limit -> limit.capacity(rateLimitRuleObj.getBucketRequests()).refillIntervally(rateLimitRuleObj.getBucketRequests(), Duration.ofSeconds(rateLimitRuleObj.getBucketDuration())))
//                        .build();
//                rateLimitBucketsMap.put(key, bucket);
//            });
//            logger.trace("Rate Limit Create Buckets Done");
//        }
//    }
//}