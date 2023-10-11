package com.parunev.linkededge.model.payload.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreparationResponse {

    private String doYouFit;
    private String doYouNotFit;
}
