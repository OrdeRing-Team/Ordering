package com.example.orderingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.ReviewDto;
import com.example.orderingproject.Dto.response.OrderDetailDto;
import com.example.orderingproject.databinding.ActivityWriteReviewBinding;
import com.google.firestore.v1.Write;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteReviewActivity extends BasicActivity {

    private ActivityWriteReviewBinding binding;

    Long restaurantId, orderId;
    String restaurantName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonFunction();
        initData();
        initView();

    }

    private void initView(){
        binding.tvStoreName.setText(restaurantName);
    }

    private void initData(){
        restaurantId = getIntent().getLongExtra("restaurantId",0);
        orderId = getIntent().getLongExtra("orderId",0);
        restaurantName = getIntent().getStringExtra("restaurantName");

        Log.e("### restaurantId ", Long.toString(restaurantId));
        Log.e("### orderId ", Long.toString(orderId));
        Log.e("### restaurantName ",restaurantName);
    }

    private void buttonFunction() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });

        binding.btnSaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgress(WriteReviewActivity.this);
                float rating = binding.ratingBar.getRating();
                String review = binding.etReview.getText().toString();
                if(!review.equals("")){
                    if(rating != 0) {
                        try {
                            new Thread() {
                                @SneakyThrows
                                public void run() {

                                    ReviewDto reviewDto = new ReviewDto(review, (byte) Float.floatToIntBits(rating));
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl("http://www.ordering.ml/api/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();

                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<ResultDto<Boolean>> call = service.addReview(restaurantId, orderId, reviewDto, null);

                                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                                        @Override
                                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                            if (response.isSuccessful()) {
                                                ResultDto<Boolean> result;
                                                result = response.body();
                                                if (result.getData()) {
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            stopProgress();
                                                            Toast.makeText(WriteReviewActivity.this, "소중한 리뷰를 작성해 주셔서 감사합니다", Toast.LENGTH_SHORT).show();
                                                            FinishWithAnim();
                                                        }
                                                    });
                                                } else {
                                                    stopProgress();
                                                    Log.e("result.getData() ", "false@@@@@@@@@@@@@");
                                                    Toast.makeText(WriteReviewActivity.this, "이미 해당 주문에 대한 리뷰를 작성했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                stopProgress();

                                                Log.e("response failed", Long.toString(orderId));
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                            stopProgress();

                                            Toast.makeText(WriteReviewActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                            Log.e("e = ", t.getMessage());

                                        }
                                    });
                                }
                            }.start();

                        } catch (Exception e) {
                            stopProgress();

                            Toast.makeText(WriteReviewActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", e.getMessage());

                        }
                    }else{
                        stopProgress();
                        Toast.makeText(WriteReviewActivity.this,"평점을 선택해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    stopProgress();
                    Toast.makeText(WriteReviewActivity.this, "리뷰를 작성해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.e("rating",Float.toString(v));
            }
        });
    }
}