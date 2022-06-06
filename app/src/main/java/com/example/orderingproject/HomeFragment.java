package com.example.orderingproject;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

    public static double longitude;
    public static double latitude;

    int currentPage = SplashActivity.adapter.getItemCount() / 2;

    String eventPageDescript;
    int listSize;

    Timer timer;
    final long DELAY_MS = 5000;  // (초기 웨이팅 타임) ex) 앱 로딩 후 5초 뒤 플립됨.
    final long PERIOD_MS = 5000; // 5초 주기로 배너 이동

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


    ExecutorService executorService = Executors.newCachedThreadPool();

    private void initButtonListener() {
        final LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        binding.btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );

                }
                else {
                    //Location location = lm.getLastKnownLocation();
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    String locationProvider = LocationManager.NETWORK_PROVIDER;
                    Location location = locationManager.getLastKnownLocation(locationProvider);
                    //location = lm.getLastKnownLocation(provider);
//                    double longitude = location.getLongitude();
//                    double latitude = location.getLatitude();
//                    double altitude = location.getAltitude();

                    //txtResult.setText("위치정보 : " + location + "\n" );

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);


                    Intent intent = new Intent(getActivity(), StoresActivity.class);
                    startActivity(intent);
                }

                //getActivity().finish();   //현재 액티비티 종료
            }
        });

        binding.btnQrCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());

                // QR코드 포맷만 스캔하도록 설정
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

                // 바코드 인식시 소리 여부
                integrator.setBeepEnabled(false);

                // 0 = 후면카메라, 1 = 전면카메라
                integrator.setCameraId(0);

                // true일때는 onActivityResult에서 QR코드 스캔한 결과값만 받는것이 아닌
                // QR코드 촬영한 이미지도 비트맵 형식으로 전달 받을 수 있다.
                integrator.setBarcodeImageEnabled(false);

                integrator.setCaptureActivity(ScannerActivity.class); //바코드 스캐너 시작
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


        /** 임시 버튼 나중에 삭제 할 것 **/
        binding.btnSeunggyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomStoreDialog dialog = new CustomStoreDialog(getActivity(), "2", "table31");
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
        binding.btnMinju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomStoreDialog dialog = new CustomStoreDialog(getActivity(), "3", "takeout");
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
        binding.btnJeonghyun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showToast(getActivity(),"더미데이터 들어가면 추가할 예정!!!");
            }
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            double altitude = location.getAltitude();

//            txtResult.setText("위치정보 : " + provider + "\n" +
//                    "위도 : " + longitude + "\n" +
//                    "경도 : " + latitude + "\n" +
//                    "고도  : " + altitude);

            Log.e("위도", String.valueOf(longitude));
            Log.e("경도", String.valueOf(latitude));

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public interface OnApplySelectedListener {
        public void onCatagoryApplySelected( int longitude);
    }

    private void initData() {
        // 배너 데이터
        if (SplashActivity.adapter != null) {
            this.listSize = SplashActivity.listSize;

            binding.vpEvent.setAdapter(SplashActivity.adapter);
            Log.e("adapterItemCount", Integer.toString(SplashActivity.adapter.getItemCount()));

            binding.vpEvent.setCurrentItem(currentPage, false);

            binding.vpEvent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // 스크롤 했을 때 현재 페이지를 설정해준다.
                    // 자동 스크롤의 경우 currentPage의 변수 값을 현재 position으로 잡는데
                    // 2페이지에서 사용자가 1페이지로 슬라이드 했을 때
                    // 자동 스크롤 시 다음 페이지를 3번이 아닌 2번으로 다시 바꿔줌
                    currentPage = position;
                    if(timer != null){
                        // 타이머 종료 후 재실
                        timer.cancel();
                        timer = null;

                        timerStart();
                    }

                    // 페이지가 스크롤(자동스크롤, 사용자 직접 스크롤 모두 포함)될 때마다 페이지 번호를 갱신
                    eventPageDescript = getString(R.string.EventViewPagerDescript, position % listSize+1, listSize);

                    binding.tvPagenum.setText(eventPageDescript);
                }
            });
        } else {
            Toast.makeText(getActivity(), "배너 로딩 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT).show();
        }

        getRecentOrderData();
    }

    private void initViews(){
        // 초기 실행 시 페이지는 1번으로 설정하고, 그 이후는 initData()의 onPageSelected를 따름
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
        // 다른 탭으로 이동 시 타이머 중지시킴
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void timerStart() {
        // Adapter 세팅 후 타이머 실행
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int nextPage = currentPage + 1;
                binding.vpEvent.setCurrentItem(nextPage, true);
                currentPage = nextPage;
            }
        };
        timer = new Timer();
        // thread에 작업용 thread 추가
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

}