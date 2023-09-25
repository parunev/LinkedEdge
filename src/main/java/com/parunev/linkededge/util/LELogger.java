package com.parunev.linkededge.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Data
public class LELogger {

    private final Logger logger;

    private static ThreadLocal<String> correlationIdTL = new ThreadLocal<>();

    private static ThreadLocal<HttpServletRequest> requestTL = new ThreadLocal<>();

    public LELogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static void setLoggerProperties(String correlationId, HttpServletRequest request){
        correlationIdTL.set(correlationId);
        requestTL.set(request);
    }

    public static void clearLoggerProperties() {
        correlationIdTL.remove();
        requestTL.remove();
    }

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public static String getCorrelationId() {
        return correlationIdTL.get();
    }

    public static HttpServletRequest getRequest() {
        return requestTL.get();
    }

    public void debug(String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(formatMessage(message), args);
        }
    }

    public void info(String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(formatMessage(message), args);
        }
    }

    public void warn(String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(formatMessage(message), args);
        }
    }

    public void error(String message, Throwable throwable, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage(message), args, throwable);
        }
    }

    private String formatMessage(String message) {
        return message + " (Correlation ID: " + getCorrelationId() + ", Client IP: " + getClientIp(getRequest()) + ")";
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
