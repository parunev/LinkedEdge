package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.util.annotations.nobadwords.NoBadWords;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@GroupSequence({AnswerRequest.class, FirstOrder.class,SecondOrder.class})
public class AnswerRequest {
    @Pattern(regexp = "^[a-zA-Z0-9äöüÄÖÜ, ;?!.+-`']*$", message = "Please avoid using special characters.", groups = FirstOrder.class)
    @NoBadWords(groups = SecondOrder.class)
    private String question;
}

interface FirstOrder{}
interface SecondOrder{}
