package com.example.orderingproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.ENUM_CLASS.OrderType;
import com.example.orderingproject.databinding.FragmentOrderlistBinding;
import com.loopeer.cardstack.CardStackView;

import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderlistFragment extends Fragment {

    private FragmentOrderlistBinding binding;
    private CardStackView mStackView;
    private LinearLayout mActionButtonContainer;
    public static List<OrderPreviewWithRestSimpleDto> pastOrderList;
    static RecyclerView orderOutRecyclerView;
//    private TestStackAdapter mTestStackAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderlistBinding.inflate(inflater, container, false);

        initData();

        return binding.getRoot();
    }

    private void initData() {

        getOrderData();
        orderOutRecyclerView = binding.rvOrderOutProgress;

    }

    private void getOrderData(){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/orders/ongoing/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> call = service.getOrderInList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<OrderPreviewWithRestSimpleDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> call, Response<ResultDto<List<OrderPreviewWithRestSimpleDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<OrderPreviewWithRestSimpleDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (result.getData().size() == 0) {
                                                binding.clEmptyOrderInProgress.setVisibility(View.VISIBLE);
                                                binding.rvOrderInProgress.setVisibility(View.GONE);
                                            } else {
                                                binding.clEmptyOrderInProgress.setVisibility(View.GONE);

                                                RecyclerView recyclerView = binding.rvOrderInProgress;
                                                OrderlistAdapter orderlistAdapter = new OrderlistAdapter(result.getData(), getContext(), binding.clEmptyOrderInProgress);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView.setAdapter(orderlistAdapter);
                                                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));
                                            }

                                            getPastOrderData();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

    public void getPastOrderData() {
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Log.e("customerId", UserInfo.getCustomerId().toString());
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/"+UserInfo.getCustomerId()+"/orders/finished/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> call = service.getOrderOutList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<OrderPreviewWithRestSimpleDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> call, Response<ResultDto<List<OrderPreviewWithRestSimpleDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<OrderPreviewWithRestSimpleDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pastOrderList = null;
                                            if (result.getData().size() == 0) {
                                                binding.clEmptyOrderOutProgress.setVisibility(View.VISIBLE);
                                                binding.rvOrderOutProgress.setVisibility(View.GONE);
                                            } else {
                                                binding.clEmptyOrderOutProgress.setVisibility(View.GONE);

                                                pastOrderList = result.getData();
                                                setPastOrderRecyclerView(pastOrderList);
                                            }

                                            binding.progressbarOrderlist.setVisibility(View.GONE);

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<OrderPreviewWithRestSimpleDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

    public static void setPastOrderRecyclerView(List<OrderPreviewWithRestSimpleDto> pastOrderList){
        PastOrderAdapter pastOrderAdapter = new PastOrderAdapter(pastOrderList, orderOutRecyclerView.getContext());
        orderOutRecyclerView.setLayoutManager(new LinearLayoutManager(orderOutRecyclerView.getContext()));
        orderOutRecyclerView.setAdapter(pastOrderAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver), new IntentFilter("testData"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getExtras().getString("data");
            Log.e("BroadcastReceiver@@@@@@@@@@@@@@@",data);

            if(data != OrderType.WAITING.toString()){
                getOrderData();
            }
        }
    };
}