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
import com.example.orderingproject.Dto.request.WaitingRegisterDto;
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

    private Long restaurantId;
    int count = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogWaitingInfoBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //Custom Dialog 배경 투명하게 -> 모서리 둥글게 커스텀했더니 각진 DialogFragment의 뒷 배경이 보이기 때문
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

        getStoreData();
        Log.e("매장아이디", String.valueOf(restaurantId));
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
                //dialogListener.onAddWaitingNumButtonClicked(waitingId, totalCount);
                Log.e("totalCount", String.valueOf(totalCount));
                requestWaitingToServer(totalCount);
                String restaurantName = String.valueOf(binding.tvStoreName.getText());
                UserInfo.setWaitingRestaurantName(restaurantName);
                Toast.makeText(getActivity(), "인원 수 " + String.valueOf(totalCount) + "명으로 웨이팅이 신청되었습니다.", Toast.LENGTH_LONG).show();
                dismiss();
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


    private void getStoreData() {
        Bundle waitingData = getArguments();

        String storeIcon = waitingData.getString("profileImageUrl");
        if (storeIcon == null) Glide.with(getContext()).load(R.drawable.icon).into(binding.ivStoreIcon);
        else Glide.with(getContext()).load(storeIcon).into(binding.ivStoreIcon);

        String storeName = waitingData.getString("storeName");
        binding.tvStoreName.setText(storeName);

        String storeId = waitingData.getString("storeId");
        restaurantId = Long.valueOf(storeId);
    }


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
                    Call<ResultDto<Boolean>> call = service.requestWaiting(restaurantId, UserInfo.getCustomerId(), waitingRegisterDto);

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
                                            Log.e("웨이팅 정보", "매장 아이디 : " + String.valueOf(restaurantId));
                                            Log.e("웨이팅 정보", "업로드 완료. 인원 수 : " + String.valueOf(count) + "명");

                                        }
                                    });
                                    Log.e("result.getData() ", Boolean.toString(result.getData()));
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

}
