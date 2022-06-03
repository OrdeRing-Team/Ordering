package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.response.OrderFoodDto;

import java.util.ArrayList;
import java.util.List;

public class OrderlistDetailFoodListAdapter extends RecyclerView.Adapter<OrderlistDetailFoodListAdapter.CustomViewHolder> {
    List<OrderFoodDto> arrayFoodList;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaymentSumPrice, tvPaymentMenuName, tvPaymentCount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPaymentMenuName = (TextView)itemView.findViewById(R.id.tv_payment_menuname);
            tvPaymentSumPrice = (TextView)itemView.findViewById(R.id.tv_payment_sumprice);
            tvPaymentCount = (TextView)itemView.findViewById(R.id.tv_payment_count);
        }
    }

    public OrderlistDetailFoodListAdapter(List<OrderFoodDto> arrayList, Context context) {
        this.arrayFoodList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new CustomViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.tvPaymentMenuName.setText(arrayFoodList.get(position).getFoodName());
        holder.tvPaymentSumPrice
                .setText(
                        Utillity.computePrice(
                                arrayFoodList.get(position).getPrice() *
                                        arrayFoodList.get(position).getCount()
                        )
                        + "원");
        holder.tvPaymentCount.setText(Integer.toString(arrayFoodList.get(position).getCount()) + "개");
    }

    @Override
    public int getItemCount() {
        return (arrayFoodList != null ? arrayFoodList.size() : 0);
    }

}

