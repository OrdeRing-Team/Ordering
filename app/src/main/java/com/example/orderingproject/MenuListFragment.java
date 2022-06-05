package com.example.orderingproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orderingproject.Dto.FoodDto;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.RepresentativeMenuDto;
import com.example.orderingproject.databinding.FragmentMenuListBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MenuListFragment extends Fragment {
    private View view;

    // false인 경우 공지사항 접은 상태
    private Boolean viewMore = false;

    //viewbinding
    private FragmentMenuListBinding binding;

    ArrayList<MenuData> menuList = new ArrayList<>();

    HashMap<Long, Long> representMenuHashMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_list, container, false);
        binding = FragmentMenuListBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        MainActivity.startProgress(getActivity());

        getMenuFromServer();
        checkNotice();
        return view;
    }

    public void getMenuFromServer(){
        // 매장 모든 음식 불러오기
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+MenuActivity.store+"/foods/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<FoodDto>>> call = service.getFood(Long.valueOf(MenuActivity.store));

                    call.enqueue(new Callback<ResultDto<List<FoodDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<FoodDto>>> call, Response<ResultDto<List<FoodDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<FoodDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            result.getData().forEach(foodDto ->{
                                                menuList.add(new MenuData(foodDto.getFoodId(), foodDto.getImageUrl(), foodDto.getFoodName(), Integer.toString(foodDto.getPrice()), foodDto.getMenuIntro(), foodDto.getSoldOut()));
                                                Log.e("매장 메뉴 정보", "foodid = " + foodDto.getFoodId() + ", data = " + foodDto.getFoodName() + ", image url = " + foodDto.getImageUrl() + ", sold out = " + foodDto.getSoldOut());
                                            });

                                            getRepresentMenuData();

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<FoodDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            MainActivity.stopProgress();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            MainActivity.stopProgress();
        }
    }

    public void getRepresentMenuData(){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+MenuActivity.store+"/representatives/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<RepresentativeMenuDto>>> call = service.getRepresentList(Long.valueOf(MenuActivity.store));

                    call.enqueue(new Callback<ResultDto<List<RepresentativeMenuDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<RepresentativeMenuDto>>> call, Response<ResultDto<List<RepresentativeMenuDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<RepresentativeMenuDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            if(result.getData().size() != 0){
                                                // 대표메뉴 리사이클러뷰 적용
                                                binding.cvRepresent.setVisibility(View.VISIBLE);

                                                result.getData().forEach(RepresentativeMenuDto ->{
                                                    representMenuHashMap.put(RepresentativeMenuDto.getFoodId(), RepresentativeMenuDto.getRepresentativeMenuId());
                                                    Log.e("대표 메뉴 리스트 받아오기 FoodId() :", Long.toString(RepresentativeMenuDto.getFoodId()));
                                                });

                                                RecyclerView recyclerView = binding.rvRepresent;
                                                RepresentMenuAdapter representMenuAdapter = new RepresentMenuAdapter(result.getData(), getActivity());
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                recyclerView.setAdapter(representMenuAdapter);
                                            }else{
                                                binding.cvRepresent.setVisibility(View.GONE);
                                            }

                                            // 전체 메뉴 리사이클러뷰 적용
                                            RecyclerView recyclerView = binding.rvMenu;
                                            MenuAdapter manageAdapter = new MenuAdapter(menuList, representMenuHashMap,getActivity());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            recyclerView.setAdapter(manageAdapter);

                                            MainActivity.stopProgress();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<RepresentativeMenuDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            MainActivity.stopProgress();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            MainActivity.stopProgress();
        }
    }

    public void checkNotice(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(MenuActivity.notice != null && !MenuActivity.notice.equals("")){
                    Log.e("clNoticeLayout", "Visible");
                    binding.clNoticeLayout.setVisibility(View.VISIBLE);
                    binding.tvNotice.setText(MenuActivity.notice);
                    binding.clNoticeViewmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!viewMore) {
                                binding.tvNotice.setMaxLines(100);
                                binding.tvNoticeMore.setText("접어두기");
                                binding.ivNoticeMore.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_up_24);
                                viewMore = true;
                            }
                            else{
                                binding.tvNotice.setMaxLines(4);
                                binding.tvNoticeMore.setText("펼쳐보기");
                                binding.ivNoticeMore.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24);
                                viewMore = false;
                            }
                        }
                    });
                }else{
                    Log.e("clNoticeLayout", "gone");

                }
            }
        },500);

    }
}