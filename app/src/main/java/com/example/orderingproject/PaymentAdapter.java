package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dialog.CustomMenuOptionDialog;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.CustomViewHolder> {
    ArrayList<BasketData> arrayPaymentList;
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

    public PaymentAdapter(ArrayList<BasketData> arrayList, Context context) {
        this.arrayPaymentList = arrayList;
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
        holder.tvPaymentMenuName.setText(arrayPaymentList.get(position).getBasketFoodName());
        holder.tvPaymentSumPrice
                .setText(
                        CustomMenuOptionDialog.computePrice(
                                arrayPaymentList.get(position).getBasketPrice() *
                                        arrayPaymentList.get(position).getBasketCount()
                        )
                        + "원");
        holder.tvPaymentCount.setText(Integer.toString(arrayPaymentList.get(position).getBasketCount()) + "개");
    }

    @Override
    public int getItemCount() {
        return (arrayPaymentList != null ? arrayPaymentList.size() : 0);
    }

}

