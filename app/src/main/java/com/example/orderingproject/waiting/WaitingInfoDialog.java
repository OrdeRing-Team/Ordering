package com.example.orderingproject.waiting;

import static android.graphics.Color.TRANSPARENT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.request.WaitingRegisterDto;
import com.example.orderingproject.Dto.response.MyWaitingInfoDto;
import com.example.orderingproject.MainActivity;
import com.example.orderingproject.R;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.DialogWaitingInfoBinding;

import java.util.List;
import java.util.zip.Inflater;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaitingInfoDialog extends DialogFragment {

    public static WaitingInfoDialog getInstance() { return new WaitingInfoDialog(); }

    private View view;
    private DialogWaitingInfoBinding binding;

    private String storeId;
    int count = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogWaitingInfoBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //Custom Dialog 배경 투명하게 -> 모서리 둥글게 커스텀했더니 각진 DialogFragment의 뒷 배경이 보이기 때문
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

        Bundle waitingData = getArguments();
        storeId = waitingData.getString("storeId");

        getStoreDataFromServer(storeId);

        initButtonListeners();

        return view;
    }


    public void initButtonListeners() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnAskWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int totalCount = Integer.parseInt(binding.tvCount.getText().toString());
                Log.e("totalCount", String.valueOf(totalCount));

                requestWaitingToServer(totalCount);

            }
        });

        binding.btnPlus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                count += 1;
                if (count == 2) {
                    buttonRelease(binding.btnMinus);
                }
                binding.tvCount.setText(String.valueOf(count));

                if (count == 20) {
                    buttonLock(binding.btnPlus);
                }
            }
        });

        binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (count != 1) {
                    count -= 1;
                    if (count == 1) buttonLock(binding.btnMinus);

                    binding.tvCount.setText(String.valueOf(count));

                    if (!binding.btnPlus.isClickable()) {
                        buttonRelease(binding.btnPlus);
                    }
                }
            }
        });
    }

    private void buttonLock (View view){
        view.setClickable(false);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.light_gray)));
    }
    private void buttonRelease (View view){
        view.setClickable(true);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.button_black)));
    }


    //웨이팅 요청하기
    private void requestWaitingToServer(int count) {
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    String url = "http://www.ordering.ml/";

                    WaitingRegisterDto waitingRegisterDto = new WaitingRegisterDto(Byte.valueOf(String.valueOf(count)));
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.requestWaiting(Long.valueOf(storeId), UserInfo.getCustomerId(), waitingRegisterDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {

                                ResultDto<Boolean> result;
                                result = response.body();

                                // result.getData()가 true이면 웨이팅 접수 성공
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("result.getData() ", Boolean.toString(result.getData()));
                                            Log.e("웨이팅 정보", "매장 아이디 : " + String.valueOf(storeId));
                                            Log.e("웨이팅 정보", "업로드 완료. 인원 수 : " + String.valueOf(count) + "명");
                                        }
                                    });
                                    String restaurantName = String.valueOf(binding.tvStoreName.getText());
                                    UserInfo.setWaitingRestaurantName(restaurantName);
                                    Toast.makeText(getActivity(), "인원 수 " + String.valueOf(count) + "명으로 웨이팅이 신청되었습니다.", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

                                // result.getData()가 false이면 이미 다른 매장 또는 현재 접수하려는 매장에 웨이팅 접수를 한 상태
                                else{
                                    Log.e("result.getData() ", Boolean.toString(result.getData()));
                                    Toast.makeText(getActivity(), "이미 웨이팅이 신청되었어요!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(getContext(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getContext(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

    private void getStoreDataFromServer(String restaurantId){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+restaurantId+"/preview/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<RestaurantPreviewDto>> call = service.storePreview(Long.parseLong(restaurantId));

                    call.enqueue(new Callback<ResultDto<RestaurantPreviewDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<RestaurantPreviewDto>> call, Response<ResultDto<RestaurantPreviewDto>> response) {

                            ResultDto<RestaurantPreviewDto> result = response.body();
                            if (response.isSuccessful()) {
                                if(result.getData() != null){
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            binding.tvStoreName.setText(String.valueOf(result.getData().getRestaurantName()));
                                            String storeIcon = result.getData().getProfileImageUrl();
                                            if(storeIcon == null){
                                                Glide.with(getContext()).load(R.drawable.icon).into(binding.ivStoreIcon);
                                            }else {
                                                Glide.with(getContext()).load(storeIcon).into(binding.ivStoreIcon);
                                            }

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getContext(),"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<RestaurantPreviewDto>> call, Throwable t) {
                            Toast.makeText(getContext(),"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                            Log.e("e = " , t.getMessage());

                            dismiss();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getContext(),"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
            Log.e("e = " , e.getMessage());

            dismiss();
        }
    }

}
