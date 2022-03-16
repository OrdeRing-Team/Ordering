package com.example.orderingproject.Dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultDto<T> {

    private int size;
    private T data;
}
