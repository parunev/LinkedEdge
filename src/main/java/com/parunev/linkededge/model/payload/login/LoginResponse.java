package com.parunev.linkededge.model.payload.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Response payload for user login", description = "Extends the BasePayload class" +
        "which means fields like 'path', 'message', 'status', 'timestamp' are also included in this response")
public class LoginResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "Jwt Access Token",
            example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJMaW5rZWRFZGdlX0FQSSIsInN1YiI6InBhcnVuZXZAZ21haWwuY29tIiwiZXhwIjoxNjk2MzM0MTgyLCJpYXQiOjE2OTYzMzM1ODIsImp0aSI6IjRmYjI1ODAxLWI1NGEtNDhhNi1iYjcwLTVlMGRkZGUxYTdiZiIsInNjb3BlIjoiUk9MRV9VU0VSIn0.pTSULuQ4SXb1sFYCpe4dEVzMYo_h7XFXFa8NaNcqazGP9bIljsx9WdRUWQQOXrcWP7mQItiu_CMHjA46d9El2Ai5dnpYAHUYtebppclkuaJntrJzFK3aSqIhVmBqKxowVgTZH4783KqfJ47TtGIWBfywaHNh0rh9A-A2DXDh5_hRoKvQY9M8v0UNbD3cLca6sUH80y-WLR9KjZaPbwwYQHIZxduBi8HHYgyAG4zCDx9eazJc8_t6lGQlz-JZJnzIaorcavRx_lA3q9Ablus05Ljj7Dy_NaGC0F83m-nP5wVY2fVGfrc2H5N1MwNkngM_ji7poc9qhKGBc3b_x-_KAw",
    type = "String")
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "Jwt Refresh Token",
            example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJMaW5rZWRFZGdlX0FQSSIsInN1YiI6InBhcnVuZXZAZ21haWwuY29tIiwiZXhwIjoxNjk2MzM0MTgyLCJpYXQiOjE2OTYzMzM1ODIsImp0aSI6IjRmYjI1ODAxLWI1NGEtNDhhNi1iYjcwLTVlMGRkZGUxYTdiZiIsInNjb3BlIjoiUk9MRV9VU0VSIn0.pTSULuQ4SXb1sFYCpe4dEVzMYo_h7XFXFa8NaNcqazGP9bIljsx9WdRUWQQOXrcWP7mQItiu_CMHjA46d9El2Ai5dnpYAHUYtebppclkuaJntrJzFK3aSqIhVmBqKxowVgTZH4783KqfJ47TtGIWBfywaHNh0rh9A-A2DXDh5_hRoKvQY9M8v0UNbD3cLca6sUH80y-WLR9KjZaPbwwYQHIZxduBi8HHYgyAG4zCDx9eazJc8_t6lGQlz-JZJnzIaorcavRx_lA3q9Ablus05Ljj7Dy_NaGC0F83m-nP5wVY2fVGfrc2H5N1MwNkngM_ji7poc9qhKGBc3b_x-_KAw",
            type = "String")
    private String refreshToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "URL for the user's secret image (QR Code)",
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAV4AAAFeAQAAAADlUEq3AAAC5UlEQVR4Xu2YW4rkMAxFBV5AluSte0lZgMGjc+V0u0Iz9M/AYFmElEs6zsdFDyc2fm/d3p6/2IFXO/BqB17twKsdeLUDr/bPYcPquKuvy2jdcHerxf/exEommPVd7fK7do2mu1t7npAJvq3gbL5mCwyefgGbNMwGOxZZFCmkdGr4U8L98v9VuWR2xS49JBs8VFbqLRFiL1vwTyANLLGqAz9dxFLBYZ5FplxCOu+6vqbQlvj3emcYcVRZBZe0um1wVerLG28mmE57KZ0U6swgNBwI2ELMRHAIpXEMgEVSoZ5CiWCcnk5SL9Imeo6prGwe27LALp34iaFcjB7GEPdcsB/JvrbMtBlPWeF/1eDeMKmi04iEgjd1XXnkzAXzG6cRpVNjHE++UmKZYKKSK4QCi0RSWb3G8fbwIw5OLSJWEfCnstodppRQ72qkk7JIYZQsekgeGN5n8YVQNBxhMF5WjycPbDGC/a5jKsvnnGZqNalgbyMF9biTUbFWRrH+lG57mDbyhLjrzU5h/KYSywNPucgfEim2KIVY22zIWeAOZ3K2WJcYxLPEkDEPPFRH7tY5hE5rvNQQ1KcAlVsauPA2R4Ptmr99dhW13+i9mWD/SynBM39DKzqtPLSgTPBQiEtCqbjqI12zGExp4K5PhfKrmhhAkg6LjYngKKsSb3n6OIZzSqcEywR/AfQZNJSMPo80huzVZHaHQ58uQOq1wLhmTSWCQ66uw8lQXrl0g89EhORPBAegyqrIqC2dQ6yfUtqrrLaHY+gEJhkBdFWlk33ovDtceK1DJdNLjSnadWxjfeeCw6ZTd5dRW1rRoxTPAhtWwymtKrukHuvPstoelmIVUp1WcoWGki4bfCOR1BtaeIm525VssWZvNpjDPJcyqoV04quoXHDRqRXzY5vO8121pnsmGMBhG8oiFnM6u7WiissDUzuavCa58MSbb6hKOiWCf2cHXu3Aqx14tQOvduDVDrza/wL/AZ/KgB4hKAVjAAAAAElFTkSuQmCC",
            type = "String",
    description = "Provides a QR Image to be displayed during the login process")
    private String secretImageUri;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "Indicates if multi-factor authentication (MFA) is enabled for the user", example ="true", type = "Boolean",
    description = "It's good to have it in the response, front-end developers will understand what's the next possible redirect")
    private boolean mfaEnabled;
}
