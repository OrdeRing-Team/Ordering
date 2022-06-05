package com.example.orderingproject.stores;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.R;

import java.util.ArrayList;

class StoreRecyclerAdapter extends RecyclerView.Adapter<StoreRecyclerAdapter.ViewHolder> {

    private ArrayList<StoreData> itemData;
    Context context;


    public StoreRecyclerAdapter(ArrayList<StoreData> itemData) {
        this.itemData = itemData;
    }

    public interface MyRecyclerViewClickListener {
        void onItemClicked(int position);
    }


    private MyRecyclerViewClickListener mListener;

    public void setOnClickListener(MyRecyclerViewClickListener listener) {
        this.mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        StoreData item = itemData.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText((CharSequence) item.getContent());
        holder.image.setImageResource(Integer.parseInt(item.getImage()));
        holder.score.setText(item.getScore());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(view.getContext(), MenuActivity.class);
                view.getContext().startActivity(intent);
                //((Activity)view.getContext()).finish();
            }
        });
//
//        if (mListener != null) {
//            final int pos = position;
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
////                    mListener.onItemClicked(pos);
////                    Intent intent= new Intent(context, MenuActivity.class);
////
////                    context.startActivity(intent);
//                    //finish();   //현재 액티비티 종료
//                }
//            });
//        }
//            holder.title.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onTitleClicked(pos);
//                }
//            });
//            holder.content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onContentClicked(pos);
//                }
//            });
//            holder.image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onImageViewClicked(pos);
//                }
//            });
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    mListener.onItemLongClicked(holder.getAdapterPosition());
//                    return true;
//                }
//            });
    }


    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        ImageView image;
        TextView score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            image = itemView.findViewById(R.id.image);
            //score = itemView.findViewById(R.id.score);

            //이미지뷰 원형으로 표시
            image.setBackground(new ShapeDrawable(new OvalShape()));
            image.setClipToOutline(true);
        }
    }

    //리스트 삭제 이벤트
    public void remove(int position) {
        try {
            itemData.remove(position);
            notifyDataSetChanged();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
