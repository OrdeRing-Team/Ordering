package com.example.orderingproject.Dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignInDto {

    private String signInId;
    private String password;
용    private String firebaseToken;
}
