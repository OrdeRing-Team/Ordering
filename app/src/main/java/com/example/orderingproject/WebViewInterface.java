package com.example.orderingproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.orderingproject.Dialog.CustomDialogOneType;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.CouponSerialNumberDto;
import com.example.orderingproject.Dto.request.SignInDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;
import com.firebase.ui.auth.data.model.User;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebViewInterface {
    private WebView mAppView;
    private Activity mContext;

    public final int TEN = 100000;
    public final int FIVE = 50000;
    public final int THREE = 30000;

    CustomDialogOneType dialog;

    public WebViewInterface(Activity activity, WebView view) {
        mAppView = view;
        mContext = activity;
    }

    @JavascriptInterface
    public void CouponIssue(int price) {
        try {
            CouponSerialNumberDto couponSerialNumberDto;
            switch(price){
                case TEN :
                    // 10먄원 쿠폰
                    couponSerialNumberDto = new CouponSerialNumberDto("d00491d25f351b000e45c3072935590d");
                    break;
                case FIVE :
                    // 5만원 쿠폰
                    couponSerialNumberDto = new CouponSerialNumberDto("c3a22dcaeaff630eaaf14ac087eb6e2b");
                    break;
                case THREE :
                    // 3만원 쿠폰
                    couponSerialNumberDto = new CouponSerialNumberDto("632224a7c23d486912388c0d9dbbe52b");
                    break;
                default:
                    couponSerialNumberDto = new CouponSerialNumberDto("");
                    break;
            }

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCusetomerId() + "/coupon/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.couponIssue(UserInfo.getCusetomerId(), couponSerialNumberDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result = response.body();
                            if(result.getData()){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        couponSuccessDialog();
                                    }
                                });
                            }
                            else{
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.showLongToast(mContext,"이미 쿠폰을 발급 받으셨습니다. 쿠폰 사용 후 재발급 받아 주시기 바랍니다.");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(mContext,"알 수 없는 오류가 발생했습니다. 다시 시도해 주세요.");
                            Log.e("e = " , t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            MainActivity.showLongToast(mContext,"[Error: NONEXISTANT Coupon SerialNum] 쿠폰을 발급받을 수 없습니다. 관리자에게 문의해 주세요.");
            Log.e("e = " , e.getMessage());
        }
    }

    private void couponSuccessDialog(){
        dialog = new CustomDialogOneType(mContext,
                "쿠폰이 발급되었습니다.",
                "쿠폰함에서 쿠폰을 확인해보세요.",
                "확인",
                closeButton);

        dialog.show();
    }
    private final View.OnClickListener closeButton = view -> {
        dialog.dismiss();
    };
}