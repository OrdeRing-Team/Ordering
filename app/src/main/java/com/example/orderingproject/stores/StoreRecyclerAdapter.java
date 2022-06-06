package com.example.orderingproject.stores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.orderingproject.favoriteStores.FavStoreAdapter;
import com.example.orderingproject.favoriteStores.FavStoreData;
import com.example.orderingproject.favoriteStores.FavStoreListActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreRecyclerAdapter extends RecyclerView.Adapter<StoreRecyclerAdapter.CustomViewHolder>{
    ArrayList<StoreData> arrayList;
    Context context;
    int position;


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업

        TextView tv_storeName, tv_storeSigMenus;
        ImageView iv_storeIcon;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_storeName = itemView.findViewById(R.id.tv_storeName);
            iv_storeIcon = itemView.findViewById(R.id.iv_storeIcon);
            tv_storeSigMenus = itemView.findViewById(R.id.tv_storeSigMenus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        Log.e("리사이클러뷰 아이템", "클릭됨.");
                        Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
                        intent.putExtra("activity", "storesActivity");
                        intent.putExtra("storeId", String.valueOf(arrayList.get(position).getRestaurantId()));
                        intent.putExtra("storeName", arrayList.get(position).getStoreName());
                        String profileImageUrl = arrayList.get(position).getStoreIcon();
                        String backgroundImageUrl = arrayList.get(position).getStoreSigMenu();

                        if(profileImageUrl!= null) {
                            intent.putExtra("profileImageUrlfromStore", profileImageUrl);
                        }

                        if(backgroundImageUrl != null) {
                            intent.putExtra("backgroundImageUrlfromStore", backgroundImageUrl);
                        }


                        context.startActivity(intent);

                    }
                }
            });
        }
    }

    public StoreRecyclerAdapter(ArrayList<StoreData> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public StoreRecyclerAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public StoreRecyclerAdapter(ArrayList<StoreData> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public StoreRecyclerAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreRecyclerAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecyclerAdapter.CustomViewHolder holder, int position) {
        // onBindViewHolder: put data of item list into xml widgets
        // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
        // position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tv_storeName.setText(arrayList.get(position).getStoreName());
        String imageURL = String.valueOf(arrayList.get(position).getStoreIcon());

        if (imageURL.equals("null")) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.icon).into(holder.iv_storeIcon);
        }
        else {
            Glide.with(holder.itemView.getContext()).load(imageURL).into(holder.iv_storeIcon);
        }

        if (arrayList.get(position).getStoreSigMenus().size() == 0) {
            holder.tv_storeSigMenus.setText("대표 메뉴가 없습니다.");
        }
        else {
            // [, ] 제거
            String content = String.valueOf(arrayList.get(position).getStoreSigMenus());
            String sigMenus = content.substring(1, content.length()-1);
            holder.tv_storeSigMenus.setText(sigMenus);
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