package com.parunev.linkededge.model.payload.interview;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID answerId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String question;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String answer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String example;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String benefits;
}
