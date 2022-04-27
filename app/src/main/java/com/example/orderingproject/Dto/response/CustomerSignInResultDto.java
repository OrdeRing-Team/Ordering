package com.example.orderingproject.Dto.response;

import static lombok.AccessLevel.PROTECTED;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class CustomerSignInResultDto {

    private Long customerId;
    private String nickname;

    public Long getCustomerId(){
        return customerId;
    }
    public String getNickname(){
        return nickname;
    }
}