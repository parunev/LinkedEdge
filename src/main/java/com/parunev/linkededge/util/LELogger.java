package com.parunev.linkededge.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
/**
 * The `LELogger` class is a custom logger utility that extends the SLF4J Logger, providing additional features
 * for logging and tracking logs within the LinkedEdge application. It allows the inclusion of a correlation ID and
 * client IP address in log messages.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Data
public class LELogger {

    /**
     * The SLF4J Logger used for actual logging.
     */
    private final Logger logger;

    /**
     * Thread-local variable for storing the correlation ID
     */
    private static ThreadLocal<String> correlationIdTL = new ThreadLocal<>();

    /**
     * Thread-local variable for storing the HTTP servlet request.
     */
    private static ThreadLocal<HttpServletRequest> requestTL = new ThreadLocal<>();

    /**
     * Thread-local variable for storing the HTTP servlet request associated with the current thread.
     */
    public LELogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Sets the correlation ID and HTTP servlet request associated with the current thread. This method is typically
     * called at the beginning of a request or task to track and correlate logs.
     *
     * @param correlationId The unique identifier used to correlate logs.
     * @param request      The HTTP servlet request associated with the current thread.
     */
    public static void setLoggerProperties(String correlationId, HttpServletRequest request){
        correlationIdTL.set(correlationId);
        requestTL.set(request);
    }

    /**
     * Clears the correlation ID and HTTP servlet request associated with the current thread. This method is typically
     * called at the end of a request or task to release resources.
     */
    public static void clearLoggerProperties() {
        correlationIdTL.remove();
        requestTL.remove();
    }

    /**
     * Generates a new unique correlation ID, typically used at the start of a request or task.
     *
     * @return A new correlation ID as a string.
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Retrieves the correlation ID associated with the current thread.
     *
     * @return The correlation ID as a string.
     */
    public static String getCorrelationId() {
        return correlationIdTL.get();
    }

    /**
     * Retrieves the HTTP servlet request associated with the current thread.
     *
     * @return The HTTP servlet request, or `null` if not available.
     */
    public static HttpServletRequest getRequest() {
        return requestTL.get();
    }

    /**
     * Logs a debug-level message with optional message formatting and arguments.
     *
     * @param message The log message with optional placeholders.
     * @param args    The arguments to fill the placeholders in the message.
     */
    public void debug(String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(formatMessage(message), args);
        }
    }

    /**
     * Logs an info-level message with optional message formatting and arguments.
     *
     * @param message The log message with optional placeholders.
     * @param args    The arguments to fill the placeholders in the message.
     */
    public void info(String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(formatMessage(message), args);
        }
    }

    /**
     * Logs a warning-level message with optional message formatting and arguments.
     *
     * @param message The log message with optional placeholders.
     * @param args    The arguments to fill the placeholders in the message.
     */
    public void warn(String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(formatMessage(message), args);
        }
    }

    /**
     * Logs an error-level message with optional message formatting, a throwable, and arguments.
     *
     * @param message   The log message with optional placeholders.
     * @param throwable The exception or throwable associated with the error.
     * @param args      The arguments to fill the placeholders in the message.
     */
    public void error(String message, Throwable throwable, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage(message), args, throwable);
        }
    }

    /**
     * Formats the log message by appending the correlation ID and client IP address, if available, to provide additional
     * context information in the log entry.
     *
     * @param message The log message to be formatted.
     * @return The formatted log message with correlation ID and client IP.
     */
    private String formatMessage(String message) {
        // Appends the correlation ID and client IP to the log message for context.
        return message + " (Correlation ID: " + getCorrelationId() + ", Client IP: " + getClientIp(getRequest()) + ")";
    }

    /**
     * Retrieves the client IP address from the provided HTTP servlet request. If the request is null or the IP address
     * cannot be determined, "Unknown" is returned.
     *
     * @param request The HTTP servlet request from which to retrieve the client IP address.
     * @return The client IP address as a string or "Unknown" if unavailable.
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            // Return "Unknown" if the request is null.
            return "Unknown";
        }

        // Attempt to retrieve the client IP address from different request headers.
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            // If still unknown, use the remote address from the request.
            clientIp = request.getRemoteAddr();
        }
        // Return the determined client IP address.
        return clientIp;
    }
}
