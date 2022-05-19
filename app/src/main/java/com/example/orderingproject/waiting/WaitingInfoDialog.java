package com.example.orderingproject.waiting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.orderingproject.Dialog.CustomMenuOptionDialogListener;
import com.example.orderingproject.R;
import com.example.orderingproject.databinding.CustomStoreInfoDialogBinding;
import com.example.orderingproject.databinding.DialogWaitingInfoBinding;

import java.util.List;
import java.util.zip.Inflater;

public class WaitingInfoDialog extends Dialog implements View.OnClickListener {

    private DialogWaitingInfoBinding binding;
    private CustomWaitingTeamNumDialogListener dialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = DialogWaitingInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public WaitingInfoDialog(@NonNull Context context) {
        super(context);
    }

//    public WaitingInfoDialog(@NonNull Context context, int themeResId) {
//        super(context, themeResId);
//    }
//
//    protected WaitingInfoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void initButtonListeners(){
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

//        binding.btnAskWaiting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int totalPrice = Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString());
//                int totalCount = Integer.parseInt(binding.tvCount.getText().toString());
//                dialogListener.onAddBasketButtonClicked(foodId, totalPrice, totalCount);
//                dismiss();
//            }
//        });
//
//        binding.btnPlus.setOnClickListener(new View.OnClickListener(){
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View view){
//                count += 1;
//                if(count == 2){
//                    buttonRelease(binding.btnMinus);
//                }
//                binding.tvCount.setText(Integer.toString(count));
//                if(count == 10){
//                    buttonLock(binding.btnPlus);
//                }
//                // 장바구니 담기 버튼 텍스트 설정
//                finalPrice = computePrice(Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString()));
//                binding.btnAddbasket.setText(finalPrice + "원 장바구니에 담기");
//            }
//        });
//
//        binding.btnMinus.setOnClickListener(new View.OnClickListener(){
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View view){
//                if(count != 1) {
//                    count -= 1;
//                    if(count == 1) buttonLock(binding.btnMinus);
//                    binding.tvCount.setText(Integer.toString(count));
//                    if (!binding.btnPlus.isClickable()) {
//                        buttonRelease(binding.btnPlus);
//                    }
//                    // 장바구니 담기 버튼 텍스트 설정
//                    finalPrice = computePrice(Integer.parseInt(price) * Integer.parseInt(binding.tvCount.getText().toString()));
//                    binding.btnAddbasket.setText(finalPrice + "원 장바구니에 담기");
//                }
//            }
//        });
    }
}
