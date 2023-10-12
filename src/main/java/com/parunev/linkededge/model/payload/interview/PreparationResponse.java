package com.parunev.linkededge.model.payload.interview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Response payload for job interview preparation insights.")
public class PreparationResponse {

    @Schema(name = "Do fit the job requirements", example = "You fit the job requirements because of your relevant experience.", type = "String")
    private String doYouFit;

    @Schema(name = "Insights on areas where you may not fit the job requirements", example = "You may not fit the job requirements due to lack of specific certification",
    type = "String")
    private String doYouNotFit;
}
