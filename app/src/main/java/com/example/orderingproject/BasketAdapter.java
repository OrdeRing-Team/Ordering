package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.CustomViewHolder> {
    ArrayList<BasketData> arrayBasketList;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvBasketPrice, tvBasketSumPrice, tvBasketMenuName, tvBasketCount;
        ImageView ivBasketMenuImage;
        Button btnDelete, btnPlus, btnMinus;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBasketPrice = itemView.findViewById(R.id.tv_basket_menuprice);
            tvBasketSumPrice = itemView.findViewById(R.id.tv_basket_sumprice);
            tvBasketMenuName = itemView.findViewById(R.id.tv_basket_menuname);
            ivBasketMenuImage = itemView.findViewById(R.id.iv_basket_menuimage);
            tvBasketCount = itemView.findViewById(R.id.tv_basket_count);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
        }
    }
    public BasketAdapter(ArrayList<BasketData> arrayList, Context context) {
        this.arrayBasketList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basket, parent, false);
        return new CustomViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tvBasketMenuName.setText(arrayBasketList.get(position).getBasketFoodName());
        holder.tvBasketPrice.setText(String.format("○ 가격 : %d원",arrayBasketList.get(position).getBasketPrice()));
        holder.tvBasketCount.setText(Integer.toString(arrayBasketList.get(position).getBasketCount()));
        int sum = arrayBasketList.get(position).getBasketPrice() * arrayBasketList.get(position).getBasketCount();
        holder.tvBasketSumPrice.setText(Integer.toString(sum));
        Glide.with(holder.itemView.getContext()).load(arrayBasketList.get(position).getBasketImageUrl()).into(holder.ivBasketMenuImage);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 장바구니 항목 삭제 처리
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수량 증가 처리
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수량 감소 처리
            }
        });
   }

    @Override
    public int getItemCount() {
        return (arrayBasketList != null ? arrayBasketList.size() : 0);
    }

}

