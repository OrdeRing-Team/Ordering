package com.example.orderingproject;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.orderingproject.databinding.FragmentKoreanFoodBinding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class KoreanFoodFragment extends Fragment {

    private ArrayList<StoreData> storeList;
    private RecyclerView recyclerView;
    private StoreRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_korean_food, container, false);
        //리사이클러뷰
        recyclerView = (RecyclerView) v.findViewById(R.id.korean_food_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(0);
        adapter = new StoreRecyclerAdapter(storeList);
        recyclerView.setAdapter(adapter);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    private void initDataset() {
        //for Test
        storeList = new ArrayList<>();
        storeList.add(new StoreData(R.drawable.icon, "박씨네 라멘트럭", "4.3","돈코츠 라멘, 탄탄 라멘, 차슈 라멘"));
        storeList.add(new StoreData(R.drawable.icon, "라화쿵부", "4.9","한그릇 마라탕, 내맘대로 마라탕"));
        storeList.add(new StoreData(R.drawable.icon, "엽기떡볶이", "5.0","로제떡볶이, 매운떡볶이, 치즈떡볶이"));
    }
}