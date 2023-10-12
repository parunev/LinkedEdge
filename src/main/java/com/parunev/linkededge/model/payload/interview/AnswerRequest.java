package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.util.annotations.nobadwords.NoBadWords;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@GroupSequence({AnswerRequest.class, FirstOrder.class,SecondOrder.class})
@Schema(name = "Request payload for answering specific questions.")
public class AnswerRequest {
    @Pattern(regexp = "^[a-zA-Z0-9äöüÄÖÜ, ;?!.+-`']*$", message = "Please avoid using special characters.", groups = FirstOrder.class)
    @NoBadWords(groups = SecondOrder.class)
    @Schema(name = "User's specific question", example = "I want to know about Object-Oriented-Programming(OOP), please provide guidance.", type = "String")
    private String question;
}

@SchemaProperty(name = "Group interface for the first order validation constraints.")
interface FirstOrder{}

@SchemaProperty(name = "Group interface for the second order validation constraints.")
interface SecondOrder{}
