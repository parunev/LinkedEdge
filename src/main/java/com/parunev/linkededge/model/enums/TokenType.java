package com.parunev.linkededge.model.enums;

/**
 * The `TokenType` enum represents different types of tokens used in the LinkedEdge application. These tokens are associated with various functionalities
 * such as authentication, confirmation, and password management.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
public enum TokenType {

    /**
     * Bearer token type for authentication and authorization.
     */
    BEARER,

    /**
     * Confirmation token type used for email and account confirmation.
     */
    CONFIRMATION,

    /**
     * Password token type for password reset and management.
     */
    PASSWORD
}
