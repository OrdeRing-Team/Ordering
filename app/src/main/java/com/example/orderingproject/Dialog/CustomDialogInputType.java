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

import com.example.orderingproject.databinding.CustomDialogInputBinding;

public class CustomDialogInputType extends Dialog {
    private CustomDialogInputBinding binding;
    private View.OnClickListener positiveButtonInputType;
    private View.OnClickListener negativeButtonInputType;

    String title;
    String contents;
    String positiveButtonText;
    String negativeButtonText;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomDialogInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etReverifyCode.requestFocus();

        TextView titleText = binding.tvTitle;
        TextView messages = binding.tvMessage;
        Button positive_btn = binding.positiveBtn;
        positive_btn.setTextColor(Color.parseColor("#0066FF"));
        Button negative_btn = binding.negativeBtn;

        titleText.setText(title);
        messages.setText(contents);
        positive_btn.setText(positiveButtonText);
        negative_btn.setText(negativeButtonText);

        positive_btn.setOnClickListener(positiveButtonInputType);
        negative_btn.setOnClickListener(negativeButtonInputType);
    }

    public CustomDialogInputType(@NonNull Context context,
                        String title,
                        String contents,
                        String positiveButtonText,
                        String negativeButtonText,
                        View.OnClickListener positiveButton,
                        View.OnClickListener negativeButton){
        super(context);

        this.title = title;
        this.contents = contents;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonInputType = positiveButton;
        this.negativeButtonInputType = negativeButton;
    }

    public String getReverifyCode(){
        return binding.etReverifyCode.getText().toString();
    }
}
