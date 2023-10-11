package com.parunev.linkededge.model.enums;

/**
 * The `ValidValue` enum represents valid values for different aspects of a user's profile in the LinkedEdge application. These values are used to
 * indicate the validity or correctness of information related to skills, education, and experience. The enum is mainly used in custom chat-gpt validators
 * to validate and assess the quality of user-provided data.
 *
 * @author Martin Parunev
 * @date October 11, 2023
 */
public enum ValidValue {
    /**
     * Valid skill value.
     */
    VALID_SKILL,

    /**
     * Valid education value.
     */
    VALID_EDUCATION,

    /**
     * Valid experience value.
     */
    VALID_EXPERIENCE

}
