package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class ReviewPreviewDto {

    private Long reviewId;
    private Long customerId;
    private String review;
    private String imageUrl;
    private String orderSummary;

    public Long getReviewId() {
        return reviewId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getReview() {
        return review;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOrderSummary() {
        return orderSummary;
    }
}