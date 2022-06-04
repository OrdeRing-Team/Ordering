package com.example.orderingproject;

import static com.example.orderingproject.ENUM_CLASS.OrderType.TABLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.Dto.response.ReviewPreviewDto;
import com.example.orderingproject.ENUM_CLASS.OrderStatus;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.CustomViewHolder>{
    List<ReviewPreviewDto> arrayList;
    Context context;
    int position;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        CardView cv_delete, cv_reviewImage;
        TextView tv_reviewerName, tv_review;
        ImageView iv_reviewImage;
        ChipGroup chipGroup;
        RatingBar ratingBar;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정

            circleImageView = itemView.findViewById(R.id.iv_review_profile);

            cv_delete = itemView.findViewById(R.id.cv_delete);
            cv_reviewImage = itemView.findViewById(R.id.cv_review_image);

            tv_reviewerName = itemView.findViewById(R.id.tv_nickname);
            tv_review = itemView.findViewById(R.id.tv_review);

            iv_reviewImage = itemView.findViewById(R.id.iv_review_image);

            chipGroup = itemView.findViewById(R.id.chip_group);

            ratingBar = itemView.findViewById(R.id.ratingBar_review_inner);
        }
    }

    public ReviewListAdapter(List<ReviewPreviewDto> arrayList) {
        this.arrayList = arrayList;
    }

    public ReviewListAdapter(int position) {
        this.position = position;
    }


    public ReviewListAdapter(List<ReviewPreviewDto> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.CustomViewHolder holder, int position) {
        holder.tv_reviewerName.setText("리뷰 작성자 이름 추가해야됨!");
//        holder.ratingBar.setRating(arrayList.get(position).get);

        if(UserInfo.getCustomerId() != arrayList.get(position).getCustomerId()){
            holder.cv_delete.setVisibility(View.GONE);
        }

        if(arrayList.get(position).getImageUrl() != null){
            Glide.with(context).load(arrayList.get(position).getImageUrl()).into(holder.iv_reviewImage);
        }else holder.cv_reviewImage.setVisibility(View.GONE);
        holder.tv_review.setText(arrayList.get(position).getReview());




        String orderSummary = arrayList.get(position).getOrderSummary();

        Log.e("##########Summary###########", orderSummary);

        String[] s1 = orderSummary.split(",");
        for(int i = 0; i<s1.length-1; i++){
            String[] s2 = s1[i].split(":");
            Chip chip = new Chip(holder.itemView.getContext());
            chip.setCheckable(false);
            chip.setCloseIconVisible(false);
            chip.setText(s2[0]);
            holder.chipGroup.addView(chip);

            Log.e("##########메뉴 chip 추가###########", s1[0]);
        }

    }


    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
