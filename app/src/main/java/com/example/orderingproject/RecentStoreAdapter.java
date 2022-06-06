package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dialog.CustomDialog;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.response.RecentOrderRestaurantDto;
import com.example.orderingproject.Dto.response.ReviewPreviewDto;
import com.example.orderingproject.review.ReviewListAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecentStoreAdapter extends RecyclerView.Adapter<RecentStoreAdapter.CustomViewHolder>{
    List<RecentOrderRestaurantDto> arrayList;
    Context context;
    int position;
    float ratingScore;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_recent_background;
        TextView tv_recent_store_name, tv_recent_rating, tv_recent_order_waiting_time;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_recent_background = itemView.findViewById(R.id.iv_recent_background);

            tv_recent_store_name = itemView.findViewById(R.id.tv_recent_store_name);
            tv_recent_order_waiting_time = itemView.findViewById(R.id.tv_recent_order_waiting_time);
            tv_recent_rating = itemView.findViewById(R.id.tv_recent_rating);
        }
    }

    public RecentStoreAdapter(List<RecentOrderRestaurantDto> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecentStoreAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_order, parent, false);
        return new RecentStoreAdapter.CustomViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecentStoreAdapter.CustomViewHolder holder, int position) {
        Glide.with(context).load(arrayList.get(position).getBackgroundImageUrl()).into(holder.iv_recent_background);
        if(arrayList.get(position).getBackgroundImageUrl() == null){
            Glide.with(context).load(R.drawable.icon).into(holder.iv_recent_background);
        }
        holder.tv_recent_store_name.setText(arrayList.get(position).getRestaurantName());
        holder.tv_recent_order_waiting_time.setText(arrayList.get(position).getOrderingWaitingTime() + "분");

        Log.e("Rating",Float.toString(arrayList.get(position).getRating()));
        holder.tv_recent_rating.setText(Float.toString(arrayList.get(position).getRating()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long restaurantId = arrayList.get(position).getRestaurantId();

                Intent intent = new Intent(context, MenuActivity.class);
                intent.putExtra("activity", "orderList");
                intent.putExtra("storeId", Long.toString(restaurantId));
                intent.putExtra("restaurantName",arrayList.get(position).getRestaurantName());
                intent.putExtra("profileImageUrl", arrayList.get(position).getProfileImageUrl());

                context.startActivity(intent);
            }
        });
    }

//    private void setRatingScore(RecentStoreAdapter.CustomViewHolder holder, Long restaurantId){
//        try{
//            new Thread(){
//                @SneakyThrows
//                public void run(){
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl("http://www.ordering.ml/api/restaurant/" + restaurantId + "/reviews/")
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//
//                    RetrofitService service = retrofit.create(RetrofitService.class);
//                    Call<ResultDto<List<ReviewPreviewDto>>> call = service.getReviewList(restaurantId);
//
//                    call.enqueue(new Callback<ResultDto<List<ReviewPreviewDto>>>() {
//                        @Override
//                        public void onResponse(Call<ResultDto<List<ReviewPreviewDto>>> call, Response<ResultDto<List<ReviewPreviewDto>>> response) {
//
//                            if (response.isSuccessful()) {
//                                ResultDto<List<ReviewPreviewDto>> result;
//                                result = response.body();
//                                if (!result.getData().isEmpty()) {
//                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            float reviewTotalRating = 0;
//
//                                            for(ReviewPreviewDto i : result.getData()){
//                                                reviewTotalRating += i.getRating();
//                                            }
//
//                                            ratingScore = reviewTotalRating / result.getData().size();
//
//                                            holder.tv_recent_rating.setText(Float.toString(ratingScore));
//                                        }
//                                    });
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResultDto<List<ReviewPreviewDto>>> call, Throwable t) {
//                            Toast.makeText(context, "최근 주문 매장의 평점을 불러오는 중에 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
//                            Log.e("e = ", t.getMessage());
//                        }
//                    });
//                }
//            }.start();
//
//        } catch (Exception e) {
//            Toast.makeText(context, "최근 주문 매장의 평점을 불러오는 중에 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
//            Log.e("e = ", e.getMessage());
//        }
//    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
