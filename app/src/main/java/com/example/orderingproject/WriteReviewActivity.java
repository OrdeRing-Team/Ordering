package com.example.orderingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteReviewActivity extends BasicActivity {

    private ActivityWriteReviewBinding binding;

    private final int GET_GALLERY_IMAGE = 200;

    Long restaurantId, orderId;
    String restaurantName;

    File imageFile;

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

                                    ReviewDto reviewDto = new ReviewDto(review, rating);
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl("http://www.ordering.ml/api/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();

                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<ResultDto<Boolean>> call = null;
                                    if(imageFile == null) call = service.addReview(restaurantId, orderId, reviewDto, null);
                                    else{
                                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile);
                                        MultipartBody.Part image = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), fileBody);
                                        call = service.addReview(restaurantId, orderId, reviewDto, image);
                                    }

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

        binding.llReviewImageBeforeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentStart();
            }
        });

        binding.llReviewImageUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentStart();
            }
        });
    }

    private void intentStart(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            Log.e("selectedImageUri",selectedImageUri.toString());
            binding.llReviewImageUploaded.setVisibility(View.VISIBLE);
            binding.llReviewImageBeforeUpload.setVisibility(View.GONE);
            binding.ivReviewImageUploaded.setImageURI(selectedImageUri);

            BitmapDrawable drawable = (BitmapDrawable) binding.ivReviewImageUploaded.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            imageFile = convertBitmapToFile(bitmap, UserInfo.getSignInId() + System.currentTimeMillis() + ".png");
            Log.e("image", "imageFile is " + imageFile);
        }
    }

    private File convertBitmapToFile(Bitmap bitmap, String fileName) {

        File storage = getCacheDir();
        File tempFile = new File(storage, fileName);

        try {
            tempFile.createNewFile();

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);

            out.close();
            return tempFile;
        } catch (FileNotFoundException e) {
            Log.e("Menu Image","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("Menu Image","IOException : " + e.getMessage());
        }
        return null;
    }
}