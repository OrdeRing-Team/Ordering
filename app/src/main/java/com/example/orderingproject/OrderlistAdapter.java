package com.example.orderingproject;

import static com.example.orderingproject.ENUM_CLASS.OrderType.TABLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ProgressBar;
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
import com.example.orderingproject.Dto.response.PreviousHistoryDto;
import com.example.orderingproject.ENUM_CLASS.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderlistAdapter extends RecyclerView.Adapter<OrderlistAdapter.CustomViewHolder>{
    List<PreviousHistoryDto> arrayList;
    Context context;
    Activity activity;
    View.OnClickListener positiveButton;
    View.OnClickListener negativeButton;

    int position;

    CustomDialog dialog;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_order_in_day, tv_order_in_type, tv_order_in_store_name, tv_order_in_menu, tv_order_in_status, tv_order_in_order_number;
        Button btn_order_in_detail, btn_order_in_cancel;
        ImageView iv_order_in_menu_image;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_order_in_day = itemView.findViewById(R.id.tv_order_in_day);
            tv_order_in_type = itemView.findViewById(R.id.tv_order_in_type);
            tv_order_in_store_name = itemView.findViewById(R.id.tv_order_in_store_name);
            tv_order_in_menu = itemView.findViewById(R.id.tv_order_in_menu);
            tv_order_in_status = itemView.findViewById(R.id.tv_order_in_status);
            tv_order_in_order_number = itemView.findViewById(R.id.tv_order_in_order_number);

            btn_order_in_detail = itemView.findViewById(R.id.btn_order_in_detail);
            btn_order_in_cancel = itemView.findViewById(R.id.btn_order_in_cancel);

            iv_order_in_menu_image = itemView.findViewById(R.id.item_image);


        }
    }

    public OrderlistAdapter(List<PreviousHistoryDto> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public OrderlistAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public OrderlistAdapter(List<PreviousHistoryDto> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
        this.activity = (Activity)context;
    }




    @NonNull
    @Override
    public OrderlistAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_in_progress, parent, false);
        return new OrderlistAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderlistAdapter.CustomViewHolder holder, int position) {


        Log.e("//==========//","//====================================================//");
        Log.e("   position  /",Integer.toString(position));
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("   진행중 주문  /",arrayList.get(position).getMyOrderNumber() + "번 주문");
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    접수 시간  /",arrayList.get(position).getReceivedTime());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    주문 종류  /",arrayList.get(position).getOrderType().toString());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    주문 상태  /",arrayList.get(position).getOrderStatus().toString());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    매장 이름  /",arrayList.get(position).getRestaurantName());
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("    주문 정보  /",arrayList.get(position).getOrderSummary());
        Log.e("//==========//","//====================================================//");

        holder.tv_order_in_menu.setText(String.valueOf(arrayList.get(position).getOrderSummary()));
        holder.tv_order_in_order_number.setText(String.format("주문번호 : %d번", arrayList.get(position).getOrderId()));
        holder.tv_order_in_type.setText(arrayList.get(position).getOrderType() == TABLE ?
                (Integer.toString(arrayList.get(position).getTableNumber())+"번 테이블") :
                "포장");
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
            holder.tv_order_in_status.setText("조리중");
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
                        "주문을 취소하시겠습니까?",
                        "주문 취소는 매장에서 주문을 승인하기 전까지만 가능합니다.",
                        "주문 취소","닫기",
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
                        Call<ResultDto<OrderPreviewDto>> call = service.orderCancel(cancelOrderId);

                        call.enqueue(new Callback<ResultDto<OrderPreviewDto>>() {
                            @Override
                            public void onResponse(Call<ResultDto<OrderPreviewDto>> call, Response<ResultDto<OrderPreviewDto>> response) {

                                if (response.isSuccessful()) {
                                    ResultDto<OrderPreviewDto> result;
                                    result = response.body();
                                    if (result.getData() != null) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @SuppressLint("DefaultLocale")
                                            @Override
                                            public void run() {
                                                MainActivity.stopProgress();
                                                Log.e("position###", Integer.toString(absolutePosition));
                                                arrayList.remove(absolutePosition);
                                                notifyItemRemoved(absolutePosition);
                                                Log.e("주문취소", "성공");
//                                                    updateProcessedRecyclerView(result.getData(),false);
//                                                    arrayList.remove(canceledPosition);
//                                                    if(arrayList.size() == 0){
//                                                        emptyreceived.setVisibility(View.VISIBLE);
//                                                    }
//                                                    notifyItemRemoved(canceledPosition);
                                                dialog.stopProgress();
                                                dialog.dismiss();
                                            }
                                        });
                                    }else{
                                        MainActivity.stopProgress();

                                        Toast.makeText(context,"주문 취소에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                        dialog.stopProgress();

                                    }
                                }
                                else{
                                    Log.e("response failed",Long.toString(cancelOrderId));
                                    dialog.stopProgress();

                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<OrderPreviewDto>> call, Throwable t) {
                                MainActivity.stopProgress();

                                Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                Log.e("e = ", t.getMessage());
                                dialog.stopProgress();

                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                MainActivity.stopProgress();

                Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", e.getMessage());
                dialog.stopProgress();

            }
        };

        negativeButton = view -> {
            dialog.dismiss();
        };

    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }

//    @SuppressLint("DefaultLocale")
//    public void updateProcessedRecyclerView(OrderPreviewDto orderPreviewDto, boolean isOrderComplete){
//        if(!isOrderComplete) {
//            orderPreviewDto.setOrderStatus(OrderStatus.CANCELED);
//        }else{
//            orderPreviewDto.setOrderStatus(OrderStatus.COMPLETED);
//        }
//        List<OrderPreviewDto> tmpList = new ArrayList<OrderPreviewDto>();
//        tmpList.add(orderPreviewDto);
//        tmpList.addAll(OrderListFragment.processedList);
//        OrderListFragment.processedList = tmpList;
//        OrderListFragment.setProcessedRecyclerView(OrderListFragment.processedList);
//        processedCount.setText(String.format("(%d)",tmpList.size()));
//    }

}
