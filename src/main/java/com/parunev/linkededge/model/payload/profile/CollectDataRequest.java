package com.parunev.linkededge.model.payload.profile;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CollectDataRequest {

    @Pattern(regexp = "https://www\\.linkedin\\.com/in/.*", message = "Not a valid LinkedIn link")
    private String profileLink;
}
