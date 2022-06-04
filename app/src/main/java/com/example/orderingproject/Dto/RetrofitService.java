package com.example.orderingproject.Dto;

import com.example.orderingproject.Dto.request.BasketPutDto;
import com.example.orderingproject.Dto.request.BasketRequestDto;
import com.example.orderingproject.Dto.request.CouponSerialNumberDto;
import com.example.orderingproject.Dto.request.CustomerSignUpDto;
import com.example.orderingproject.Dto.request.OrderDto;
import com.example.orderingproject.Dto.request.PasswordChangeDto;
import com.example.orderingproject.Dto.request.PhoneNumberDto;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.request.SignInDto;
import com.example.orderingproject.Dto.request.VerificationDto;
import com.example.orderingproject.Dto.request.WaitingRegisterDto;
import com.example.orderingproject.Dto.response.BasketFoodDto;
import com.example.orderingproject.Dto.response.BookmarkPreviewDto;
import com.example.orderingproject.Dto.response.CouponDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;
import com.example.orderingproject.Dto.response.MyWaitingInfoDto;
import com.example.orderingproject.Dto.response.OrderDetailDto;
import com.example.orderingproject.Dto.response.OrderPreviewDto;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.Dto.response.RepresentativeMenuDto;
import com.example.orderingproject.Dto.response.RestaurantInfoDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

	// 로그인
   	@POST("/api/customer/signin")
	Call<ResultDto<CustomerSignInResultDto>> customerSignIn(@Body SignInDto signInDto);

	// 회원탈퇴
	@DELETE("/api/customer/{customerId}")
	Call<ResultDto<Boolean>> deleteaccount(@Path("customerId") Long customerId);

   	// 회원가입
	@POST("/api/customer/signup")
	Call<ResultDto<Long>> customerSignUp(@Body CustomerSignUpDto customerSignUpDto);

	// 문자인증 수신
	@POST("/api/customer/verification/get")
	Call<ResultDto<Boolean>> phoneNumber(@Body PhoneNumberDto phoneNumberDto);

	// 문자인증 체크
	@POST("/api/customer/verification/check")
	Call<ResultDto<Boolean>> verification(@Body VerificationDto verificationDto);

	// 쿠폰 발급
	@POST("/api/customer/{customer_Id}/coupon")
	Call<ResultDto<Boolean>> couponIssue(@Path("customer_Id") Long customerId, @Body CouponSerialNumberDto couponSerialNumberDto);

	// 쿠폰 목록 불러오기
	@POST("/api/customer/{customer_Id}/my_coupons")
	Call<ResultDto<List<CouponDto>>> getCouponList(@Path("customer_Id") Long customerId);

	// 쿠폰 사용(삭제)
	@DELETE("/api/customer/coupon/{couponId}")
	Call<ResultDto<Boolean>> useCoupon(@Path("couponId") Long couponId);

	// 매장 다이얼로그 호출
	@POST("/api/restaurant/{restaurant_id}/preview")
	Call<ResultDto<RestaurantPreviewDto>> storePreview(@Path("restaurant_id") Long restaurantId);

	// 비밀번호 변경
	@PUT("/api/customer/{customer_id}/password")
	Call<ResultDto<Boolean>> changePassword(@Path("customer_id") Long customerId, @Body PasswordChangeDto passwordChangeDto);

	// 전화번호 변경
	@PUT("/api/customer/{customer_id}/phone_number")
	Call<ResultDto<Boolean>> reverify(@Path("customer_id") Long customerId, @Body PhoneNumberDto phoneNumberDto);

	// 매장 모든 음식 불러오기
	@POST("/api/restaurant/{restaurantId}/foods")
	Call<ResultDto<List<FoodDto>>> getFood(@Path("restaurantId") Long restaurantId);

	// 대표메뉴 리스트 가져오기
	@GET("/api/restaurant/{restaurant_id}/representatives")
	Call<ResultDto<List<RepresentativeMenuDto>>> getRepresentList(@Path("restaurant_id") Long restaurantId);

	// 매장 공지사항, 좌표 가져오기
	@GET("/api/restaurant/{restaurant_id}/info")
	Call<ResultDto<RestaurantInfoDto>> getStoreNoticeAndCoordinate(@Path("restaurant_id") Long restaurantId);

	// 장바구니 메뉴 추가
	// 쿼리가 포함된 주소는 아래와 같이 사용
	// http://www.ordering.ml/api/order/basket?customer_id={customer_id}&restaurant_id={restaurant_id}
	// 참고문서 : https://jaejong.tistory.com/38
	@POST("api/order/basket")
	Call<ResultDto<Boolean>> addBasket(@Query(value = "customer_id") Long customerId,
									   @Query(value = "restaurant_id") Long restaurantId,
									   @Body BasketRequestDto basketRequestDto);

	// 장바구니 메뉴 삭제
	@DELETE("api/order/basket/{basketId}")
	Call<ResultDto<Boolean>> deleteBasket(@Path("basketId") Long basketId,
									   @Query(value = "customer_id") Long customerId);

	// 장바구니 목록 불러오기
	@POST("/api/customer/{customerId}/baskets")
	Call<ResultDto<List<BasketFoodDto>>> getBasketList(@Path("customerId") Long customerId);

	// 장바구니 주문 요청
	@POST("api/order")
	Call<ResultDto<Long>> orderRequest(@Query(value = "customer_id") Long customerId, @Body OrderDto orderDto);

	// 장바구니 수량 변경
	@PUT("/api/order/baskets")
	Call<ResultDto<Boolean>> modifyBasketCount(@Query(value = "customer_id") Long customerId,
											   @Body List<BasketPutDto> countChangedList);

	// 고객 주문 취소
	@POST("/api/order/{order_id}/cancel")
	Call<ResultDto<OrderPreviewWithRestSimpleDto>> orderCancel(@Path("order_id") Long orderId);

	// 내 주문 내역(진행 중) 리스트 가져오기
	@GET("/api/customer/{customerId}/orders/ongoing")
	Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> getOrderInList(@Path("customerId") Long customerId);

	// 내 주문 내역(완료) 리스트 가져오기
	@GET("/api/customer/{customerId}/orders/finished")
	Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> getOrderOutList(@Path("customerId") Long customerId);

	// 주문 상세 정보 반환 API
	@GET("/api/customer/order/{orderId}/detail")
	Call<ResultDto<OrderDetailDto>> getOrderDetail(@Path("orderId") Long orderId);

	/** 웨이팅 관련 함수 **/
	// 웨이팅 요청
	// http://www.ordering.ml/api/waiting?restaurant_id={restaurant_id}&customer_id={customer_id}
	@POST("api/waiting")
	Call<ResultDto<Boolean>> requestWaiting(@Query(value = "restaurant_id") Long restaurantId,
											@Query(value = "customer_id") Long customerId,
											@Body WaitingRegisterDto waitingRegisterDto);

	// 웨이팅 정보 불러오기
	@POST("/api/customer/{customer_id}/waiting")
	Call<ResultDto<MyWaitingInfoDto>> getWaitingInfo(@Path("customer_id") Long customer_id);

	// 웨이팅 취소
	@DELETE("/api/waiting/{waiting_id}")
	Call<ResultDto<Boolean>> deleteWaiting(@Path("waiting_id") Long waiting_id);


	/** 찜 관련 함수 **/
	// 매장 찜하기
	// http://www.ordering.ml/api/customer/{customerId}/bookmark?restaurant_id={restaurant_id}
	@POST("/api/customer/{customerId}/bookmark")
	Call<ResultDto<Long>> setFavStore(@Path("customerId") Long customerId, @Query(value = "restaurant_id") Long restaurant_id);

	// 매장 찜 취소
	@DELETE("/api/customer/bookmark/{bookmarkId}")
	Call<ResultDto<Boolean>> deleteFavStore(@Path("bookmarkId") Long bookmarkId);

	// 찜한 매장 리스트 불러오기
	@GET("/api/customer/{customerId}/bookmarks")
	Call<ResultDto<List<BookmarkPreviewDto>>> getFavStoreList(@Path("customerId") Long customerId);

}