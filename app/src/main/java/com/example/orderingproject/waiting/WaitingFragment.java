package com.example.orderingproject.waiting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.MyWaitingInfoDto;
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.R;
import com.example.orderingproject.StartActivity;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.FragmentWaitingBinding;
import com.firebase.ui.auth.data.model.User;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaitingFragment extends Fragment {

    private View view;
    private FragmentWaitingBinding binding;

    String restaurantId, restaurantName, profileImgUrl, backgroundImgUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWaitingBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        Log.e("CustomerId", String.valueOf(UserInfo.getCustomerId()));
        getWaitingInfo();


        btnClickFunction();

        return view;
    }

    private void btnClickFunction() {
        binding.btnCancelWaiting.setOnClickListener(view -> {
            deleteDialog();
        });

        binding.tvStoreName.setOnClickListener(view -> {
            Log.e("btnStoreName", "is clicked.");
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra("activity", "waitingFrag");
            intent.putExtra("storeId", restaurantId);
            intent.putExtra("storeName", restaurantName);
            String profileImageUrl = profileImgUrl;
            String backgroundImageUrl = backgroundImgUrl;
            if(profileImageUrl!= null) {
                intent.putExtra("profileImageUrlfromFav", profileImageUrl);
            }
            if(backgroundImageUrl != null) {
                intent.putExtra("backgroundImageUrlfromFav", backgroundImageUrl);
            }

            getActivity().startActivity(intent);

        });

        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWaitingInfo();
            }
        });
    }


    // 웨이팅 정보 불러오기
    private void getWaitingInfo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/waiting/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<MyWaitingInfoDto>> call = retrofitService.getWaitingInfo(UserInfo.getCustomerId());

        call.enqueue(new Callback<ResultDto<MyWaitingInfoDto>>() {
            @Override
            public void onResponse(Call<ResultDto<MyWaitingInfoDto>> call, Response<ResultDto<MyWaitingInfoDto>> response) {
                ResultDto<MyWaitingInfoDto> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        if (result.getData() == null) {
                            Log.e("myWaitingNumber", "is null");
                            binding.viewWaitingNone.setVisibility(View.VISIBLE);
                            binding.viewWaiting.setVisibility(View.GONE);
                        }
                        else {
                            Log.e("myWaitingNumber", String.valueOf(result.getData().getMyWaitingNumber()));
                            UserInfo.setWaitingId(result.getData().getWaitingId());
                            binding.viewWaitingNone.setVisibility(View.GONE);
                            binding.viewWaiting.setVisibility(View.VISIBLE);
                            binding.tvWaitingNum.setText(String.valueOf(result.getData().getMyWaitingNumber()));
                            binding.tvEstimatedWaitingTime.setText(result.getData().getEstimatedWaitingTime() + " 분");
                            binding.tvNumInFrontOfMe.setText(result.getData().getNumInFrontOfMe() + " 팀");
                            binding.tvStoreName.setText(result.getData().getRestaurantName() + " >");
                            String storeIcon = result.getData().getProfileImageUrl();
                            if (storeIcon == null) Glide.with(getActivity()).load(R.drawable.icon).into(binding.ivStoreIcon);
                            else Glide.with(getActivity()).load(storeIcon).into(binding.ivStoreIcon);

                            // 예상 호출 시간 계산 알고리즘
                            String date = result.getData().getWaitingRegisterTime();
                            String time = date.substring(date.length()-8, date.length());
                            String registerHour = time.substring(0, 2);
                            String registerMin = time.substring(3, 5);
                            Log.e("waiting register time", registerHour + ":" + registerMin);

                            int waitingTime = result.getData().getEstimatedWaitingTime();
                            int hour = Integer.parseInt(registerHour), min = Integer.parseInt(registerMin) + waitingTime;
                            if (min >= 60) {
                                while (min >= 60) {
                                    hour += 1;
                                    min -= 60;
                                }
                            }
                            String callHour = String.valueOf(hour);
                            String callMinute = String.valueOf(min);
                            if (min < 10) {
                                callMinute = "0" + callMinute;
                            }
                            if (hour < 10) {
                                callHour = "0" + callHour;
                            }
                            binding.tvCallTime.setText(callHour + ":" + callMinute);


                            // MenuActivity에 넘겨주기 위한 값 저장
                            restaurantId = String.valueOf(result.getData().getRestaurantId());
                            restaurantName = result.getData().getRestaurantName();
                            profileImgUrl = result.getData().getProfileImageUrl();
                            backgroundImgUrl = result.getData().getBackgroundImageUrl();

                        }

                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<MyWaitingInfoDto>> call, Throwable t) {
                Toast.makeText(getActivity(), "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });

    }


    // 웨이팅 취소하기
    private void deleteData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/waiting/" + UserInfo.getWaitingId() + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        Call<ResultDto<Boolean>> call;
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        call = retrofitService.deleteWaiting(UserInfo.getWaitingId());

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "웨이팅이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(getActivity(), "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });
    }

    public void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.WaitingCancelDialogStyle);
        builder.setMessage("웨이팅을 취소하시겠습니까?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData();
                ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new WaitingFragment()).commit();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}