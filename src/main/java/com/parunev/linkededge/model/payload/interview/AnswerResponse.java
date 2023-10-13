package com.parunev.linkededge.model.payload.interview;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Response payload for answering specific questions.")
public class AnswerResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "Unique identifier for the answer", example = "c4a8cf7-9efc-4a62-9a0d-2b27d51c64ab", type = "UUID")
    private UUID answerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "The question to which the chat-gpt has responded", example = "I want to know about Object-Oriented-Programming(OOP), please provide guidance.", type = "String")
    private String question;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "The answer response provided from chat-gpt", example = "Object Oriented Programming (OOP) is a programming paradigm that organizes code into objects, " +
            "which are instances of classes. It is essential to understand the four main principles of OOP: encapsulation, inheritance, polymorphism, and abstraction.",
    type = "String")
    private String answer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "Example how to use the information from the question", example = "Let's say you are interviewing for a software engineering position, and the interviewer asks you to explain OOP." +
            " You can provide an example using a car. " +
            "You can explain that a car is an object that has properties (such as color, model, and speed)" +
            " and behaviors (such as accelerating, braking, and turning). The car class represents the blueprint for creating car objects," +
            " and each car object is an instance of that class. This example demonstrates how OOP allows us to model real-world entities and their interactions in code.",
    type = "String")
    private String example;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "Benefits of knowing this questions and material", example = "Understanding OOP is beneficial in an interview because it showcases your ability to design and " +
            "structure code in a modular and reusable manner. It allows you to break down complex problems into smaller, manageable parts, " +
            "making your code more maintainable and scalable. Additionally, OOP promotes code reusability through inheritance and polymorphism, " +
            "reducing redundancy and improving efficiency. Employers value candidates who can apply OOP principles as it leads to cleaner, " +
            "more organized code and fosters collaboration among team members.",
    type = "String")
    private String benefits;
}
