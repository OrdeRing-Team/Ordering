package com.example.orderingproject;

import static com.example.orderingproject.ENUM_CLASS.OrderType.TABLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dialog.CustomDialog;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.OrderPreviewDto;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.ENUM_CLASS.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderlistAdapter extends RecyclerView.Adapter<OrderlistAdapter.CustomViewHolder>{
    List<OrderPreviewWithRestSimpleDto> arrayList;
    Context context;
    Activity activity;
    View.OnClickListener positiveButton;
    View.OnClickListener negativeButton;

    ConstraintLayout emptyTexts;

    int position;

    CustomDialog dialog;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_order_in_day, tv_order_in_type, tv_order_in_store_name,
                tv_order_in_menu, tv_order_in_status, tv_order_in_order_number, tv_order_in_price;
        Button btn_order_in_detail, btn_order_in_cancel;
        ImageView iv_order_in_menu_image;

        // ???????????? ??????
        ConstraintLayout cl_review_write;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item ??? ?????? ?????? ????????? ??????
            tv_order_in_day = itemView.findViewById(R.id.tv_order_in_day);
            tv_order_in_type = itemView.findViewById(R.id.tv_order_in_type);
            tv_order_in_store_name = itemView.findViewById(R.id.tv_order_in_store_name);
            tv_order_in_menu = itemView.findViewById(R.id.tv_order_in_menu);
            tv_order_in_status = itemView.findViewById(R.id.tv_order_in_status);
            tv_order_in_order_number = itemView.findViewById(R.id.tv_order_in_order_number);
            tv_order_in_price = itemView.findViewById(R.id.tv_order_in_price);

            btn_order_in_detail = itemView.findViewById(R.id.btn_order_in_detail);
            btn_order_in_cancel = itemView.findViewById(R.id.btn_order_in_cancel);

            iv_order_in_menu_image = itemView.findViewById(R.id.item_image);

        }
    }

    public OrderlistAdapter(List<OrderPreviewWithRestSimpleDto> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public OrderlistAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public OrderlistAdapter(List<OrderPreviewWithRestSimpleDto> arrayList, Context context, ConstraintLayout emptyTexts) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
        this.activity = (Activity)context;
        this.emptyTexts = emptyTexts;
    }




    @NonNull
    @Override
    public OrderlistAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater??? xml?????????. viewHolder ??????.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_in_progress, parent, false);
        return new OrderlistAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderlistAdapter.CustomViewHolder holder, int position) {

        Log.e("//==========//","//====================================================//");
        Log.e("   position  /",Integer.toString(position));
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("   ????????? ??????  /",arrayList.get(position).getMyOrderNumber() + "??? ??????");
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    ?????? ??????  /",arrayList.get(position).getReceivedTime());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    ?????? ??????  /",arrayList.get(position).getOrderType().toString());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    ?????? ??????  /",arrayList.get(position).getOrderStatus().toString());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    ?????? ??????  /",arrayList.get(position).getRestaurantName());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    ?????? ??????  /",arrayList.get(position).getOrderSummary());
        Log.e("//==========//","//====================================================//");

        String[] orderSummarySplitArr = arrayList.get(position).getOrderSummary().split(",");
        String[] orderSummaryFirstMenuSplitArr = orderSummarySplitArr[0].split(":");


        int orderSummaryOtherMenuCount = orderSummarySplitArr.length-2;
        if(orderSummarySplitArr.length == 2) {
            holder.tv_order_in_menu.setText(orderSummarySplitArr[0]);
            holder.tv_order_in_price.setText(orderSummarySplitArr[1]);
        }else{
            holder.tv_order_in_menu.setText(String.format("%s ??? %d???", orderSummaryFirstMenuSplitArr[0],orderSummaryOtherMenuCount));
            holder.tv_order_in_price.setText(orderSummarySplitArr[orderSummarySplitArr.length -1]);
        }

        holder.tv_order_in_store_name.setText(arrayList.get(position).getRestaurantName());
        holder.tv_order_in_order_number.setText(String.format("???????????? : %d???", arrayList.get(position).getMyOrderNumber()));
        holder.tv_order_in_type.setText(arrayList.get(position).getOrderType() == TABLE ?
                (Integer.toString(arrayList.get(position).getTableNumber())+"??? ?????????") : "??????");
        holder.tv_order_in_day.setText(arrayList.get(position).getReceivedTime());
        Glide.with(activity).load(arrayList.get(position).getProfileUrl()).into(holder.iv_order_in_menu_image);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder.iv_order_in_menu_image.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),40);
                }
            });

            holder.iv_order_in_menu_image.setClipToOutline(true);
        }

        if(arrayList.get(position).getCheckTime() != null){
            holder.tv_order_in_status.setText("?????????");
            holder.tv_order_in_status.setTextColor(context.getColor(R.color.blue));
            holder.btn_order_in_cancel.setVisibility(View.GONE);
        }

        holder.btn_order_in_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("#####position#####", Integer.toString(holder.getAbsoluteAdapterPosition()));
                Log.e("orderID", Long.toString(arrayList.get(holder.getAbsoluteAdapterPosition()).getOrderId()));
                dialog = new CustomDialog(
                        context,
                        "????????? ?????????????????????????",
                        "?????? ????????? ???????????? ????????? ???????????? ???????????? ???????????????.",
                        "?????? ??????","??????",
                        positiveButton,negativeButton, "#FF0000",holder.getAbsoluteAdapterPosition());

                dialog.show();
            }
        });

        positiveButton = view -> {

            dialog.showProgress();
            try {
                new Thread() {
                    @SneakyThrows
                    public void run() {
                        int absolutePosition = dialog.getAbsolutePosition();
                        Long cancelOrderId = arrayList.get(absolutePosition).getOrderId();
                        Log.e("absolutePosition",Integer.toString(absolutePosition));
                        Log.e("cancelOrderId", cancelOrderId.toString());
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/order/"+ cancelOrderId +"/cancel/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<OrderPreviewWithRestSimpleDto>> call = service.orderCancel(cancelOrderId);

                        call.enqueue(new Callback<ResultDto<OrderPreviewWithRestSimpleDto>>() {
                            @Override
                            public void onResponse(Call<ResultDto<OrderPreviewWithRestSimpleDto>> call, Response<ResultDto<OrderPreviewWithRestSimpleDto>> response) {

                                if (response.isSuccessful()) {
                                    ResultDto<OrderPreviewWithRestSimpleDto> result;
                                    result = response.body();
                                    if (result.getData() != null) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @SuppressLint("DefaultLocale")
                                            @Override
                                            public void run() {
                                                MainActivity.stopProgress();
                                                Log.e("position###", Integer.toString(absolutePosition));

                                                updateOrderOutRecyclerView(result.getData());
                                                arrayList.remove(absolutePosition);
                                                notifyItemRemoved(absolutePosition);
                                                if(arrayList.isEmpty()){
                                                    emptyTexts.setVisibility(View.VISIBLE);
                                                }
                                                Log.e("????????????", "??????");
                                                dialog.stopProgress();
                                                dialog.dismiss();
                                            }
                                        });
                                    }else{
                                        MainActivity.stopProgress();

                                        Toast.makeText(context,"?????? ????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                                        dialog.stopProgress();

                                    }
                                }
                                else{
                                    Log.e("response failed",Long.toString(cancelOrderId));
                                    dialog.stopProgress();

                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<OrderPreviewWithRestSimpleDto>> call, Throwable t) {
                                MainActivity.stopProgress();

                                Toast.makeText(context, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                                Log.e("e = ", t.getMessage());
                                dialog.stopProgress();

                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                MainActivity.stopProgress();

                Toast.makeText(context, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                Log.e("e = ", e.getMessage());
                dialog.stopProgress();

            }
        };

        negativeButton = view -> {
            dialog.dismiss();
        };

        holder.btn_order_in_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int absolutePosition = holder.getAbsoluteAdapterPosition();
                Intent intent = new Intent(context, OrderlistDetailActivity.class);
                intent.putExtra("orderId", arrayList.get(absolutePosition).getOrderId());
                context.startActivity(intent);
            }
        });

        holder.tv_order_in_store_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MenuActivity.class);
                intent.putExtra("activity", "orderList");
                intent.putExtra("storeId", Long.toString(arrayList.get(holder.getAbsoluteAdapterPosition()).getRestaurantId()));
                intent.putExtra("restaurantName",arrayList.get(holder.getAbsoluteAdapterPosition()).getRestaurantName());
                intent.putExtra("profileImageUrl", arrayList.get(holder.getAbsoluteAdapterPosition()).getProfileUrl());

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list??? ?????? ????????? ?????? return
        return (arrayList != null ? arrayList.size() : 0);
    }

    @SuppressLint("DefaultLocale")
    public void updateOrderOutRecyclerView(OrderPreviewWithRestSimpleDto orderPreviewWithRestSimpleDto){

        orderPreviewWithRestSimpleDto.setOrderStatus(OrderStatus.CANCELED);

        List<OrderPreviewWithRestSimpleDto> tmpList = new ArrayList<OrderPreviewWithRestSimpleDto>();
        tmpList.add(orderPreviewWithRestSimpleDto);
        tmpList.addAll(OrderlistFragment.pastOrderList);
        OrderlistFragment.pastOrderList = tmpList;
        OrderlistFragment.setPastOrderRecyclerView(OrderlistFragment.pastOrderList);
    }


}
