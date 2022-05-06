package com.example.orderingproject.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.orderingproject.databinding.CustomStoreInfoDialogBinding;

public class CustomStoreDialog extends Dialog {
    private CustomStoreInfoDialogBinding binding;
    private View.OnClickListener positiveButton;
    private View.OnClickListener negativeButton;

    String title;
    String contents;
    String positiveButtonText;
    String negativeButtonText;
    TextView messages;
    String contentsColor;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomStoreInfoDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public CustomStoreDialog(@NonNull Context context,
                             String store, String service){
        super(context);

//        getStoreInfo();
    }

//    private void getStoreInfo
}
