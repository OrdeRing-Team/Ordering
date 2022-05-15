package com.example.orderingproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dialog.CustomMenuOptionDialog;
import com.example.orderingproject.Dialog.CustomStoreDialog;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.CustomViewHolder> {
    ArrayList<MenuData> arrayList;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업
        TextView tvName, tvPrice, tvIntro, tvSoldout;
        ImageView ivMenu;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정

            tvName = itemView.findViewById(R.id.item_name);
            tvPrice = itemView.findViewById(R.id.item_price);
            tvIntro = itemView.findViewById(R.id.item_intro);
            ivMenu = itemView.findViewById(R.id.item_image);
            tvSoldout = itemView.findViewById(R.id.item_soldout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Log.e("menu", "item clicked.");
                    }
                }
            });
        }
    }

    public MenuAdapter(ArrayList<MenuData> arrayList) {
    // adapter constructor
        this.arrayList = arrayList;
    }


    public MenuAdapter(ArrayList<MenuData> arrayList, Context context) {
    // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }




    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
    // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
    // onBindViewHolder: put data of item list into xml widgets
    // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
    // position에 해당하는 data, viewHolder의 itemView에 표시함

        holder.tvName.setText(arrayList.get(position).getName());
        holder.tvPrice.setText(CustomMenuOptionDialog.computePrice(Integer.parseInt(arrayList.get(position).getPrice())));
        holder.tvIntro.setText(String.valueOf(arrayList.get(position).getIntro()));

        // arrayList에 저장된 메뉴 이미지 url을 imageURL변수에 저장하고 Glide로 iv에 set
        String imageURL = String.valueOf(arrayList.get(position).getIv_menu());
        Glide.with(holder.itemView.getContext()).load(imageURL).into(holder.ivMenu);

        // 품절 정보 불러와서 true이면 리사이클러뷰에 "품절" 출력하기
        boolean soldout = arrayList.get(position).getSoldout();
        if (soldout == true) {
            holder.tvSoldout.setVisibility(View.VISIBLE);
            holder.tvSoldout.setText("품절");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomMenuOptionDialog dialog = new CustomMenuOptionDialog(view.getContext(),
                        arrayList.get(position).getName(),
                        String.valueOf(arrayList.get(position).getIntro()),
                        imageURL,
                        String.valueOf(arrayList.get(position).getPrice()),
                        arrayList.get(position).getFoodId());
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
    // getItemCount: return the size of the item list
    // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }

    public void deleteItem(int position) {
        /* view에서 삭제 */
        arrayList.remove(position);
        notifyDataSetChanged();
    }

}

