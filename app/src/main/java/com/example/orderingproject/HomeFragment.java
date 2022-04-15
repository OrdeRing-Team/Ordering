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

        // progressBar를 위해 ui 완료될 때 까지 숨김
        binding.scrollView.setVisibility(View.GONE);
        showProgress();

        goStore();
        initData();
        return view;
    }

    private void goStore() {
        binding.btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), StoresActivity.class);
                startActivity(intent);
                getActivity().finish();   //현재 액티비티 종료
            }
        });
    }


    private void initData(){
        // 이벤트,쿠폰 배너에 사용되는 remoteConfig
        // remoteConfig를 통해 이미지 url(파베 스토리지에 업로드된 사진)을 실시간 가져온다.
        // 기본적으로 Fetch의 시간 간격은 12시간이다.
        // 앱 사용자가 remoteConfig를 한번 받아오면 12시간 동안은 최신화 할 수 없다는 말이다
        // 지금은 Test단계이기 때문에 0으로 설정 <- 바로바로 최신화가 반영됨
        // 너무 많은 요청 시 파베 자체에서 앱을 블락 시킬 수 있기 때문에 개발을 마친 상황에선 시간을 조정해야함
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            // fetchAndActivate가 성공 했을 때
                            try {
                                List<EventsDto> eventDto = parseEventsJson(remoteConfig.getString("events"));
                                EventPagerAdapter adapter = new EventPagerAdapter((ArrayList<EventsDto>) eventDto,getActivity());
                                binding.vpEvent.setAdapter(adapter);
                                binding.vpEvent.setCurrentItem(adapter.getItemCount()/2 -1,false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "배너 로딩 중 오류가 발생했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        hideProgress();
        binding.scrollView.setVisibility(View.VISIBLE);
        Log.e("fetch&Activate","Completed");
    }

    private List<EventsDto> parseEventsJson(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        List<EventsDto> urls = new ArrayList<>();
        for(int index = 0; index < jsonArray.length(); index++){
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            if(jsonObject != null){
                EventsDto eventsDto = new EventsDto();
                JSONObject object = (JSONObject) jsonArray.get(index);
                String imageUrl = (String)object.get("imageUrl");
                String loadUrl = (String)object.get("loadUrl");
                eventsDto.setUrls(imageUrl,loadUrl);
                urls.add(eventsDto);
            }
        }
        return urls;
    }

    public void showProgress(){
        binding.progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgress(){
        binding.progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}