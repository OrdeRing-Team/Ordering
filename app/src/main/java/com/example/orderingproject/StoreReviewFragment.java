package com.example.orderingproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orderingproject.databinding.FragmentStoreReviewBinding;

public class StoreReviewFragment extends Fragment {

    private View view;
    private FragmentStoreReviewBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStoreReviewBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        binding.btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WriteReviewActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}