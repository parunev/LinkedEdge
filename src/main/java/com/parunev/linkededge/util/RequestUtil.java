package com.parunev.linkededge.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class for retrieving information about the current HTTP request, such as the request URI.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // This class should not be instantiated as it contains only static utility methods.
public class RequestUtil {

    /**
     * Gets the current HTTP request's URI (Uniform Resource Identifier).
     *
     * @return A string representing the request URI.
     */
    public static String getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest().getRequestURI();
    }
}
