package com.example.orderingproject.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.BasketDto;
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.R;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.CustomDialogMenuOptionBinding;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CustomMenuOptionDialog extends Dialog {
    private CustomDialogMenuOptionBinding binding;
    Context mContext;

    String menuName;
    String menuImageUrl;
    String menuInfo;
    String price;
    String finalPrice;
    Long foodId;
    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomDialogMenuOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonLock(binding.btnMinus);
        initViews();
        initButtonListeners();
    }

    public void initButtonListeners(){
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnAddbasket.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view){
                try {
                    new Thread() {
                        @SneakyThrows
                        public void run() {
                            String url = "http://www.ordering.ml/";
                            int totalPrice = Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString());
                            int totalCount = Integer.parseInt(binding.tvCount.getText().toString());
                            BasketDto basketDto = new BasketDto(foodId, totalPrice, totalCount);
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<ResultDto<Boolean>> call = service.addBasket(UserInfo.getCustomerId(),Long.valueOf(MenuActivity.store), basketDto);

                            call.enqueue(new Callback<ResultDto<Boolean>>() {
                                @Override
                                public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                    if (response.isSuccessful()) {
                                        ResultDto<Boolean> result;
                                        result = response.body();
                                        if (result.getData()) {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mContext,"장바구니에 메뉴를 추가했습니다.",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            Log.e("result.getData() ", Boolean.toString(result.getData()));
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                    Toast.makeText(mContext, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    Log.e("e = ", t.getMessage());
                                }
                            });
                        }
                    }.start();

                } catch (Exception e) {
                    Toast.makeText(mContext, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    Log.e("e = ", e.getMessage());
                }

                dismiss();
            }
        });

        binding.btnPlus.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view){
                count += 1;
                if(count == 2){
                    buttonRelease(binding.btnMinus);
                }
                binding.tvCount.setText(Integer.toString(count));
                if(count == 10){
                    buttonLock(binding.btnPlus);
                }
                // 장바구니 담기 버튼 텍스트 설정
                finalPrice = computePrice(Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString()));
                binding.btnAddbasket.setText(finalPrice + "원 장바구니에 담기");
            }
        });

        binding.btnMinus.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view){
                if(count != 1) {
                    count -= 1;
                    if(count == 1) buttonLock(binding.btnMinus);
                    binding.tvCount.setText(Integer.toString(count));
                    if (!binding.btnPlus.isClickable()) {
                        buttonRelease(binding.btnPlus);
                    }
                    // 장바구니 담기 버튼 텍스트 설정
                    finalPrice = computePrice(Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString()));
                    binding.btnAddbasket.setText(finalPrice + "원 장바구니에 담기");
                }
            }
        });
    }

    public CustomMenuOptionDialog(@NonNull Context context,
                                  String menuName, String menuInfo, String menuImageUrl, String price, Long foodId){
        super(context);
        mContext = context;
        this.menuName = menuName;
        this.menuInfo = menuInfo;
        this.menuImageUrl = menuImageUrl;
        this.price = price;
        this.foodId = foodId;
    }

    @SuppressLint({"SetTextI18n", "ObsoleteSdkInt"})
    private void initViews(){
        // 메뉴 이미지 설정
        Glide.with(getContext()).load(menuImageUrl).into(binding.ivMenuimage);
        // 둥근 모서리 이미지뷰에서는 scaleType 변경 시 둥근 모서리가 해제됨. 따라서 코드상에서 다시 설정 해준다.
        // 아래 메서드는 API 21 이상부터 사용 가능
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            binding.ivMenuimage.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),40);
                }
            });

            binding.ivMenuimage.setClipToOutline(true);
        }

        // 메뉴 이름 설정
        binding.tvMenuname.setText(menuName);

        // 메뉴 소개 설정
        binding.tvMenuinfo.setText(menuInfo);

        // 장바구니 담기 버튼 텍스트 설정
        finalPrice = computePrice(Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString()));
        binding.btnAddbasket.setText(finalPrice + "원 장바구니에 담기");

    }

    public static String computePrice(int resultInt){
        StringBuilder sb = new StringBuilder();
        String resultStr = Integer.toString(resultInt);
        int length = resultStr.length();
        // 7654321  ->  7,654,321
        for(int i = 0; i < length; i++){
            sb.append(resultStr.charAt(i));
            if((length - (i + 1)) % 3 == 0 && i != length - 1) sb.append(",");
        }
        Log.e("sb = ", sb.toString());
        return sb.toString();
    }

    private void buttonLock(View view){
            view.setClickable(false);
            view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.light_gray)));
    }
    private void buttonRelease(View view){
        view.setClickable(true);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.button_black)));
    }
}
