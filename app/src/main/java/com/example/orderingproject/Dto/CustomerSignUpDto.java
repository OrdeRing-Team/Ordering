package com.example.orderingproject.Dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerSignUpDto {

    private String nickname;
    private String signInId;
    private String password;
    private String phoneNumber;
}
