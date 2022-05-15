package com.example.orderingproject;

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
    ArrayList<BasketData> arrayList;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvSumPrice, tvMenuName, tvCount;
        ImageView ivMenuImage;
        Button btnDelete, btnPlus, btnMinus;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPrice = itemView.findViewById(R.id.tv_menuprice);
            tvSumPrice = itemView.findViewById(R.id.tv_sumprice);
            tvMenuName = itemView.findViewById(R.id.tv_menuname);
            ivMenuImage = itemView.findViewById(R.id.iv_menuimage);
            tvCount = itemView.findViewById(R.id.tv_count);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
        }
    }

    public BasketAdapter(ArrayList<BasketData> arrayList) {
        this.arrayList = arrayList;
    }

    public BasketAdapter(ArrayList<BasketData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basket, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tvMenuName.setText(arrayList.get(position).getFoodName());
        holder.tvPrice.setText(arrayList.get(position).getPrice());
        holder.tvCount.setText(arrayList.get(position).getCount());
        int sum = arrayList.get(position).getPrice() * arrayList.get(position).getCount();
        holder.tvSumPrice.setText(sum);
        Glide.with(holder.itemView.getContext()).load(arrayList.get(position).getImageUrl()).into(holder.ivMenuImage);

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
        return (arrayList != null ? arrayList.size() : 0);
    }

}

