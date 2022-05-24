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
import com.example.orderingproject.Dto.request.BasketRequestDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.CustomViewHolder> {
    static ArrayList<BasketData> arrayBasketList;
    static Map<Long, Integer> hm = new HashMap<>();
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
        hm.clear();
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
        holder.tvBasketPrice
                .setText("○ 가격 : " +
                        Utillity.computePrice(
                                arrayBasketList.get(position).getBasketPrice()
                        )
                        + "원");
        holder.tvBasketCount.setText(Integer.toString(arrayBasketList.get(position).getBasketCount()));

        BasketActivity.orderCount += arrayBasketList.get(position).getBasketCount();
        BasketActivity.setOrderCount();

        int sum = arrayBasketList.get(position).getBasketPrice() * arrayBasketList.get(position).getBasketCount();
        holder.tvBasketSumPrice.setText(Utillity.computePrice(sum)+"원");
        Glide.with(holder.itemView.getContext()).load(arrayBasketList.get(position).getBasketImageUrl()).into(holder.ivBasketMenuImage);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newPosition = holder.getAbsoluteAdapterPosition();
                holder.btnDelete.setClickable(false);
                try {
                    new Thread() {
                        @SneakyThrows
                        public void run() {
                            String url = "http://www.ordering.ml/";

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<ResultDto<Boolean>> call =
                                    service.deleteBasket(
                                            arrayBasketList.get(newPosition).getBasketId(),
                                            UserInfo.getCustomerId());

                            call.enqueue(new Callback<ResultDto<Boolean>>() {
                                @Override
                                public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                    if (response.isSuccessful()) {
                                        ResultDto<Boolean> result;
                                        result = response.body();
                                        if (result.getData()) {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.e("position", Integer.toString(newPosition));
                                                    UserInfo.minusBasketCount(arrayBasketList.get(newPosition).getBasketCount());
                                                    BasketActivity.orderCount -= Integer.parseInt(holder.tvBasketCount.getText().toString());
                                                    BasketActivity.setOrderCount();
                                                    arrayBasketList.remove(newPosition);
                                                    notifyItemRemoved(newPosition);
                                                    if(arrayBasketList.isEmpty()){
                                                        BasketActivity.setEmptyView();
                                                    }
                                                }
                                            });
                                            Log.e("result.getData() ", Boolean.toString(result.getData()));
                                        }else{
                                            notifyItemInserted(newPosition);
                                            holder.btnDelete.setClickable(true);
                                            Log.e("result.getData() ", "false");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                    notifyItemInserted(newPosition);
                                    holder.btnDelete.setClickable(true);
                                    Toast.makeText(holder.itemView.getContext(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    Log.e("e = ", t.getMessage());
                                }
                            });
                        }
                    }.start();

                } catch (Exception e) {
                    notifyItemInserted(newPosition);
                    holder.btnDelete.setClickable(true);
                    Toast.makeText(holder.itemView.getContext(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    Log.e("e = ", e.getMessage());
                }
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCount(holder, holder.getAbsoluteAdapterPosition());
                UserInfo.addBasketCount(1);
                BasketActivity.orderCount++;
                BasketActivity.setOrderCount();
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusCount(holder, holder.getAbsoluteAdapterPosition());
                UserInfo.minusBasketCount(1);
                BasketActivity.orderCount--;
                BasketActivity.setOrderCount();
            }
        });
        buttonCheck(holder);
    }

    @SuppressLint("SetTextI18n")
    public void addCount(CustomViewHolder holder, int position) {
        int currentCount = Integer.parseInt(holder.tvBasketCount.getText().toString());

        currentCount++;
        BasketActivity.totalCount++;

        updateHashMap(currentCount, position);
        updatePrice(currentCount, position, holder);

        holder.tvBasketCount.setText(Integer.toString(currentCount));

        buttonCheck(holder);
    }

    @SuppressLint("SetTextI18n")
    public void minusCount(CustomViewHolder holder, int position) {
        int currentCount = Integer.parseInt(holder.tvBasketCount.getText().toString());

        currentCount--;
        BasketActivity.totalCount--;

        updateHashMap(currentCount, position);
        updatePrice(currentCount, position, holder);

        holder.tvBasketCount.setText(Integer.toString(currentCount));

        buttonCheck(holder);
    }

    private void updateHashMap(int currentCount, int position){
        String msg = "position : " + Integer.toString(position) + "  value : " + Integer.toString(currentCount);
        if(currentCount != arrayBasketList.get(position).getBasketCount()){
            hm.put(arrayBasketList.get(position).getBasketId(), currentCount);
            Log.e("hashMap put", msg);
        }else{
            hm.remove(arrayBasketList.get(position).getBasketId());
            Log.e("hashMap Removed", msg);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updatePrice(int currentCount, int position, CustomViewHolder holder){
        int changedPrice = arrayBasketList.get(position).getBasketPrice() * currentCount;
        holder.tvBasketSumPrice.setText(Utillity.computePrice(changedPrice) + "원");
    }

    private void buttonCheck(CustomViewHolder holder){
        if (holder.tvBasketCount.getText().equals("1")) {
            buttonLock(holder, false);
        }
        else{
            buttonRelease(holder, false);
        }
        if(Integer.parseInt(holder.tvBasketCount.getText().toString()) >= 99){
            buttonLock(holder, true);
        }else{
            buttonRelease(holder, true);
        }
    }

    private void buttonLock(CustomViewHolder holder, Boolean isPlusButton) {
        if (isPlusButton) {
            holder.btnPlus.setClickable(false);
            holder.btnPlus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray)));
        } else {
            holder.btnMinus.setClickable(false);
            holder.btnMinus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray)));
        }
    }

    private void buttonRelease(CustomViewHolder holder, Boolean isPlusButton) {
        if (isPlusButton) {
            holder.btnPlus.setClickable(true);
            holder.btnPlus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_black)));
        } else {
            holder.btnMinus.setClickable(true);
            holder.btnMinus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_black)));
        }
    }

    @Override
    public int getItemCount() {
        return (arrayBasketList != null ? arrayBasketList.size() : 0);
    }

    public static Map<Long, Integer> getCountChangedHashMap(){
        return hm;
    }
}

