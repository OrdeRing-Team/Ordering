package com.example.orderingproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.orderingproject.Dto.EventsDto;
import com.example.orderingproject.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View view;

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initData();
        goStore();
        return view;
    }

    private void goStore() {
        binding.btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoresActivity.class);
                startActivity(intent);
                getActivity().finish();   //현재 액티비티 종료
            }
        });
    }

    private void initData() {
        if (SplashActivity.adapter != null) {
            binding.vpEvent.setAdapter(SplashActivity.adapter);
            Log.e("adapterItemCount",Integer.toString(SplashActivity.adapter.getItemCount()));
            binding.vpEvent.setCurrentItem(SplashActivity.adapter.getItemCount() / 2, false);
        }else{
            Toast.makeText(getActivity(), "배너 로딩 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}