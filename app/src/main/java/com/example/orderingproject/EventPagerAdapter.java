package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.EventDto;

import java.util.ArrayList;

public class EventPagerAdapter extends RecyclerView.Adapter<EventPagerAdapter.EventViewHolder> {
    private ArrayList<String> eventList;
    Context context;


    @NonNull
    @Override
    public EventPagerAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPagerAdapter.EventViewHolder holder, int position) {
        // 각 position에 해당하는 url을 리스트로부터 받아온다.
        String imageUrl = eventList.get(position);

        // 해당 url 값으로 이미지를 배치시킨다.
        Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.ivEvent);
    }

    @Override
    public int getItemCount() {
        return (eventList != null ? eventList.size() : 0);
    }

    public EventPagerAdapter(ArrayList<String> arrayList) {
        this.eventList = arrayList;
    }

    public EventPagerAdapter(ArrayList<String> arrayList, Context context) {
        this.eventList = arrayList;
        this.context = context;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        ImageView ivEvent;

        public EventViewHolder(@NonNull View view) {
            super(view);
            ivEvent = view.findViewById(R.id.iv_event);
        }
    }
}

