package com.example.orderingproject;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;

import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.orderingproject.Dialog.CustomStoreDialog;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.response.RecentOrderRestaurantDto;
import com.example.orderingproject.Dto.response.ReviewPreviewDto;
import com.example.orderingproject.databinding.FragmentHomeBinding;
import com.example.orderingproject.favoriteStores.FavStoreListActivity;
import com.example.orderingproject.review.ReviewListAdapter;
import com.example.orderingproject.stores.StoresActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private View view;

    private FragmentHomeBinding binding;

    int currentPage = SplashActivity.adapter.getItemCount() / 2;

    String eventPageDescript;
    int listSize;

    Timer timer;
    final long DELAY_MS = 5000;  // (?????? ????????? ??????) ex) ??? ?????? ??? 5??? ??? ?????????.
    final long PERIOD_MS = 5000; // 5??? ????????? ?????? ??????

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        initData();
        initViews();
        initButtonListener();

        return view;
    }

    private void initButtonListener() {

        binding.btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), StoresActivity.class));
                //getActivity().finish();   //?????? ???????????? ??????
            }
        });

        binding.btnQrCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());

                // QR?????? ????????? ??????????????? ??????
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

                // ????????? ????????? ?????? ??????
                integrator.setBeepEnabled(false);

                // 0 = ???????????????, 1 = ???????????????
                integrator.setCameraId(0);

                // true????????? onActivityResult?????? QR?????? ????????? ???????????? ???????????? ??????
                // QR?????? ????????? ???????????? ????????? ???????????? ?????? ?????? ??? ??????.
                integrator.setBarcodeImageEnabled(false);

                integrator.setCaptureActivity(ScannerActivity.class); //????????? ????????? ??????
                integrator.setOrientationLocked(true);

                integrator.initiateScan();
            }
        });

        binding.btnCouponBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CouponActivity.class);
                intent.putExtra("from", "HomeFragment");
                startActivity(intent);
            }
        });

        binding.btnFavStores.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), FavStoreListActivity.class));
        });


//        /** ?????? ?????? ????????? ?????? ??? ??? **/
//        binding.btnSeunggyu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CustomStoreDialog dialog = new CustomStoreDialog(getActivity(), "2", "table31");
//                dialog.show();
//                Window window = dialog.getWindow();
//                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//            }
//        });
//        binding.btnMinju.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CustomStoreDialog dialog = new CustomStoreDialog(getActivity(), "3", "takeout");
//                dialog.show();
//                Window window = dialog.getWindow();
//                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//            }
//        });
//        binding.btnJeonghyun.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MainActivity.showToast(getActivity(),"??????????????? ???????????? ????????? ??????!!!");
//            }
//        });
    }


    public interface OnApplySelectedListener {
        public void onCatagoryApplySelected( int longitude);
    }

    private void initData() {
        // ?????? ?????????
        if (SplashActivity.adapter != null) {
            this.listSize = SplashActivity.listSize;

            binding.vpEvent.setAdapter(SplashActivity.adapter);
            Log.e("adapterItemCount", Integer.toString(SplashActivity.adapter.getItemCount()));

            binding.vpEvent.setCurrentItem(currentPage, false);

            binding.vpEvent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // ????????? ?????? ??? ?????? ???????????? ???????????????.
                    // ?????? ???????????? ?????? currentPage??? ?????? ?????? ?????? position?????? ?????????
                    // 2??????????????? ???????????? 1???????????? ???????????? ?????? ???
                    // ?????? ????????? ??? ?????? ???????????? 3?????? ?????? 2????????? ?????? ?????????
                    currentPage = position;
                    if(timer != null){
                        // ????????? ?????? ??? ??????
                        timer.cancel();
                        timer = null;

                        timerStart();
                    }

                    // ???????????? ?????????(???????????????, ????????? ?????? ????????? ?????? ??????)??? ????????? ????????? ????????? ??????
                    eventPageDescript = getString(R.string.EventViewPagerDescript, position % listSize+1, listSize);

                    binding.tvPagenum.setText(eventPageDescript);
                }
            });
        } else {
            Toast.makeText(getActivity(), "?????? ?????? ??? ????????? ??????????????????.",
                    Toast.LENGTH_SHORT).show();
        }

        getRecentOrderData();
    }

    private void initViews(){
        // ?????? ?????? ??? ???????????? 1????????? ????????????, ??? ????????? initData()??? onPageSelected??? ??????
        eventPageDescript = getString(R.string.EventViewPagerDescript,1, listSize);
        binding.tvPagenum.setText(eventPageDescript);
    }

    @Override
    public void onResume() {
        super.onResume();

        timerStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        // ?????? ????????? ?????? ??? ????????? ????????????
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void timerStart() {
        // Adapter ?????? ??? ????????? ??????
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int nextPage = currentPage + 1;
                binding.vpEvent.setCurrentItem(nextPage, true);
                currentPage = nextPage;
            }
        };
        timer = new Timer();
        // thread??? ????????? thread ??????
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }


    private void getRecentOrderData(){
        try{
            new Thread(){
                @SneakyThrows
                public void run(){
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/"+UserInfo.getCustomerId()+"/orders/recent/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<RecentOrderRestaurantDto>>> call = service.getRecentOrderList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<RecentOrderRestaurantDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<RecentOrderRestaurantDto>>> call, Response<ResultDto<List<RecentOrderRestaurantDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<RecentOrderRestaurantDto>> result;
                                result = response.body();
                                if (!result.getData().isEmpty()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            RecyclerView recyclerView = binding.rvRecentOrder;
                                            RecentStoreAdapter recentStoreAdapter = new RecentStoreAdapter(result.getData(), getContext());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                                            recyclerView.setAdapter(recentStoreAdapter);
                                            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));
                                        }
                                    });
                                }else{
                                    binding.rvRecentOrder.setVisibility(View.GONE);
                                    binding.llEmptyRecent.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<RecentOrderRestaurantDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

}