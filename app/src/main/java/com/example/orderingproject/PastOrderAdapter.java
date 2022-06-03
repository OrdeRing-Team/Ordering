package com.example.orderingproject;

import static com.example.orderingproject.ENUM_CLASS.OrderType.TABLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.ENUM_CLASS.OrderStatus;

import java.util.List;

public class PastOrderAdapter extends RecyclerView.Adapter<PastOrderAdapter.CustomViewHolder>{
    List<OrderPreviewWithRestSimpleDto> arrayList;
    Context context;
    int position;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_orderOutCancel, tv_orderOutDay, tv_orderOutType, tv_orderOutOrderNumber, tv_orderOutStoreName, tv_orderOutMenu, tv_orderOutPrice;
        ImageView iv_orderOutImage;

        ConstraintLayout cl_reviewWrite;

        Button btn_orderOutDetail;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_orderOutCancel = itemView.findViewById(R.id.tv_order_out_cancel);
            tv_orderOutDay = itemView.findViewById(R.id.tv_order_out_day);
            tv_orderOutType = itemView.findViewById(R.id.tv_order_out_type);
            tv_orderOutOrderNumber = itemView.findViewById(R.id.tv_order_out_order_number);
            tv_orderOutStoreName = itemView.findViewById(R.id.tv_order_out_store_name);
            tv_orderOutMenu = itemView.findViewById(R.id.tv_order_out_menu);
            tv_orderOutPrice = itemView.findViewById(R.id.tv_order_out_price);

            iv_orderOutImage = itemView.findViewById(R.id.item_image);

            btn_orderOutDetail = itemView.findViewById(R.id.btn_order_out_detail);
            cl_reviewWrite = itemView.findViewById(R.id.cl_review_write);
        }
    }

    public PastOrderAdapter(List<OrderPreviewWithRestSimpleDto> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public PastOrderAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public PastOrderAdapter(List<OrderPreviewWithRestSimpleDto> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PastOrderAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_out_progress, parent, false);
        return new PastOrderAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastOrderAdapter.CustomViewHolder holder, int position) {
        if(arrayList.get(position).getOrderStatus() == OrderStatus.CANCELED){
            holder.tv_orderOutCancel.setVisibility(View.VISIBLE);
        }
        holder.tv_orderOutDay.setText(String.valueOf(arrayList.get(position).getReceivedTime()));
        holder.tv_orderOutType.setText(arrayList.get(position).getOrderType() == TABLE ?
                (Integer.toString(arrayList.get(position).getTableNumber())+"번 테이블") :
                "포장");
        holder.tv_orderOutOrderNumber.setText(String.format("주문번호 : %d번", arrayList.get(position).getOrderId()));
        Glide.with(context).load(arrayList.get(position).getProfileUrl()).into(holder.iv_orderOutImage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            holder.iv_orderOutImage.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),40);
                }
            });

            holder.iv_orderOutImage.setClipToOutline(true);
        }
        String[] orderSummarySplitArr = arrayList.get(position).getOrderSummary().split(",");
        String[] orderSummaryFirstMenuSplitArr = orderSummarySplitArr[0].split(":");
        int orderSummaryOtherMenuCount = orderSummarySplitArr.length-2;
        if(orderSummarySplitArr.length == 2) {
            holder.tv_orderOutMenu.setText(orderSummarySplitArr[0]);
            holder.tv_orderOutPrice.setText(orderSummarySplitArr[1]);
        }else{
            holder.tv_orderOutMenu.setText(String.format("%s 외 %d개", orderSummaryFirstMenuSplitArr[0],orderSummaryOtherMenuCount));
            holder.tv_orderOutPrice.setText(orderSummarySplitArr[orderSummarySplitArr.length -1]);
        }

        holder.cl_reviewWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, ReviewWriteActivity.class);
//                context.startActivity(intent);
            }
        });

        holder.btn_orderOutDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int absolutePosition = holder.getAbsoluteAdapterPosition();
                Intent intent = new Intent(context, OrderlistDetailActivity.class);
                intent.putExtra("orderId", arrayList.get(absolutePosition).getOrderId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }
}
