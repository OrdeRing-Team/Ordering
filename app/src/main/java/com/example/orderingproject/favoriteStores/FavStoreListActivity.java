package com.example.orderingproject.favoriteStores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.orderingproject.BasicActivity;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.BookmarkPreviewDto;
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.R;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.ActivityBasketBinding;
import com.example.orderingproject.databinding.ActivityFavStoreListBinding;
import com.example.orderingproject.databinding.ActivityWriteReviewBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavStoreListActivity extends BasicActivity {

    private ActivityFavStoreListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavStoreListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initButtonFunction();
        getFavStoreListFromServer();

    }

    private void initButtonFunction() {
        binding.btnBack.setOnClickListener(view -> {
            FinishWithAnim();
        });
    }

    // 찜 목록 불러오기
    private void getFavStoreListFromServer() {
        ArrayList<FavStoreData> favStoreList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/bookmarks/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<ResultDto<List<BookmarkPreviewDto>>> call = service.getFavStoreList(UserInfo.getCustomerId());

        call.enqueue(new Callback<ResultDto<List<BookmarkPreviewDto>>>() {
            @Override
            public void onResponse(Call<ResultDto<List<BookmarkPreviewDto>>> call, Response<ResultDto<List<BookmarkPreviewDto>>> response) {

                if (response.isSuccessful()) {
                    ResultDto<List<BookmarkPreviewDto>> result;
                    result = response.body();
                    if (result.getData() != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                result.getData().forEach(bookmarkPreviewDto ->{
                                    favStoreList.add(new FavStoreData(bookmarkPreviewDto.getProfileImageUrl(), bookmarkPreviewDto.getRestaurantName(), bookmarkPreviewDto.getRepresentativeMenus()));
                                });

                                // 찜 매장 리스트가 없을 경우 (예외 처리)
                                if (favStoreList.size() == 0) {
                                    binding.tvEmpty.setVisibility(View.VISIBLE);
                                }

                                RecyclerView recyclerView = binding.rvFavStores;
                                FavStoreAdapter favStoreAdapter = new FavStoreAdapter(favStoreList, FavStoreListActivity.this);
                                recyclerView.setLayoutManager(new LinearLayoutManager(FavStoreListActivity.this));
                                recyclerView.setAdapter(favStoreAdapter);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultDto<List<BookmarkPreviewDto>>> call, Throwable t) {
                Toast.makeText(FavStoreListActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }
}