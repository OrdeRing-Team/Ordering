package com.example.orderingproject.waiting;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWaitingBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        Log.e("CustomerId", String.valueOf(UserInfo.getCustomerId()));
        getWaitingInfo();

        btnClickFuntion();

        return view;
    }

    private void btnClickFuntion() {
        binding.btnCancelWaiting.setOnClickListener(view -> {
            deleteDialog();
        });

        binding.tvStoreName.setOnClickListener(view -> {
            //startActivity(new Intent(getActivity(), MenuActivity.class));
            Log.e("btnStoreName", "is clicked.");
        });
    }


    // 웨이팅 정보 불러오기
    private void getWaitingInfo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/waiting/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<MyWaitingInfoDto>> call = retrofitService.getWaitingInfo(UserInfo.getCustomerId());

        call.enqueue(new Callback<ResultDto<MyWaitingInfoDto>>() {
            @Override
            public void onResponse(Call<ResultDto<MyWaitingInfoDto>> call, Response<ResultDto<MyWaitingInfoDto>> response) {
                ResultDto<MyWaitingInfoDto> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (result.getData() == null) {
                            Log.e("myWaitingNumber", "is null");
                            binding.viewWaitingNone.setVisibility(View.VISIBLE);
                            binding.viewWaiting.setVisibility(View.GONE);
                            //UserInfo.setWaitingId(null);

                        }

                        else {
                            Log.e("myWaitingNumber", String.valueOf(result.getData().getMyWaitingNumber()));
                            UserInfo.setWaitingId(result.getData().getWaitingId());
                            binding.viewWaitingNone.setVisibility(View.GONE);
                            binding.viewWaiting.setVisibility(View.VISIBLE);
                            binding.tvWaitingNum.setText(String.valueOf(result.getData().getMyWaitingNumber()));
                            binding.tvEstimatedWaitingTime.setText(String.valueOf(result.getData().getEstimatedWaitingTime()) + " 분");
                            binding.tvNumInFrontOfMe.setText(String.valueOf(result.getData().getNumInFrontOfMe()) + " 팀");
                            binding.tvStoreName.setText(String.valueOf(result.getData().getRestaurantName()) + " >");
                            String storeIcon = result.getData().getProfileImageUrl();
                            if (storeIcon == null) Glide.with(getActivity()).load(R.drawable.icon).into(binding.ivStoreIcon);
                            else Glide.with(getActivity()).load(storeIcon).into(binding.ivStoreIcon);

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