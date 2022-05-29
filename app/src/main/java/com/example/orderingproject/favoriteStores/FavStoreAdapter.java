package com.example.orderingproject.favoriteStores;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class FavStoreAdapter extends RecyclerView.Adapter<FavStoreAdapter.CustomViewHolder>{
    ArrayList<FavStoreData> arrayList;
    Context context;
    int position;



    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업

        TextView tv_storeName, tv_storeSigMenus;
        CircularImageView iv_storeIcon;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_storeName = itemView.findViewById(R.id.tv_storeName);
            iv_storeIcon = itemView.findViewById(R.id.iv_storeIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, MenuActivity.class);
                        intent.putExtra("activity", "favActivity");
                        intent.putExtra("storeId", String.valueOf(arrayList.get(position).getRestaurantId()));
                        intent.putExtra("storeName", arrayList.get(position).getStoreName());
                        String profileImageUrl = arrayList.get(position).getStoreIcon();
                        String backgroundImageUrl = arrayList.get(position).getStoreSigMenu();
                        if(profileImageUrl!= null) {
                            intent.putExtra("profileImageUrlfromFav", profileImageUrl);
                        }
                        if(backgroundImageUrl != null) {
                            intent.putExtra("backgroundImageUrlfromFav", backgroundImageUrl);
                        }

                        context.startActivity(intent);

                    }
                }
            });
        }
    }

    public FavStoreAdapter(ArrayList<FavStoreData> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public FavStoreAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public FavStoreAdapter(ArrayList<FavStoreData> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }




    @NonNull
    @Override
    public FavStoreAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav_store, parent, false);
        return new FavStoreAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavStoreAdapter.CustomViewHolder holder, int position) {
        // onBindViewHolder: put data of item list into xml widgets
        // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
        // position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tv_storeName.setText(String.valueOf(arrayList.get(position).getStoreName()));
        String storeIcon = arrayList.get(position).getStoreIcon();
        if (storeIcon == null) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.icon).into(holder.iv_storeIcon);
        }
        else {
            Glide.with(holder.itemView.getContext()).load(storeIcon).into(holder.iv_storeIcon);
        }

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
        notifyItemRemoved(position);
    }

}
