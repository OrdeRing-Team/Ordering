package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orderingproject.Dto.FoodDto;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.RepresentativeMenuDto;
import com.example.orderingproject.ENUM_CLASS.RestaurantType;
import com.example.orderingproject.databinding.FragmentMenuListBinding;
import com.example.orderingproject.databinding.FragmentStoreInfoBinding;

import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StoreInfoFragment extends Fragment {
    private View view;

    private FragmentStoreInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_store_info, container, false);
        binding = FragmentStoreInfoBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initView();

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void initView(){
        binding.tvStoreInfoStoreName.setText(MenuActivity.restaurantNameForInfo);
        binding.tvStoreInfoLocation.setText(MenuActivity.address);
        binding.tvStoreInfoOwnerName.setText(MenuActivity.ownerName);
        binding.tvStoreInfoOrderWaitingTime.setText(Integer.toString(MenuActivity.orderWaitingTime)+"분");
        if(MenuActivity.restaurantType == RestaurantType.FOR_HERE_TO_GO) {
            binding.tvStoreInfoRestauranttype.setText("매장식사, 포장");
        }else binding.tvStoreInfoRestauranttype.setText("포장");
        binding.tvStoreInfoTableCount.setText(Integer.toString(MenuActivity.tableCount)+"개");
    }

}