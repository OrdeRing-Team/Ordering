package com.example.orderingproject.Dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class PreviousHistoryDto extends OrderPreviewDto {

    private Long restaurantId;
    private String profileUrl;
    private String restaurantName;

}