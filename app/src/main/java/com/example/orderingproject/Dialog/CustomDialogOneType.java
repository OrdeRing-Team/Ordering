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

import com.example.orderingproject.databinding.CustomDialogBinding;
import com.example.orderingproject.databinding.CustomDialogOneBinding;

public class CustomDialogOneType extends Dialog {
    private CustomDialogOneBinding binding;
    private View.OnClickListener closeButton;

    String title;
    String contents;
    String closeButtonText;
    TextView messages;
    String contentsColor;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomDialogOneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView titleText = binding.tvTitle;
        messages = binding.tvMessage;
        Button close_btn = binding.btnClose;

        titleText.setText(title);
        messages.setText(contents);
        close_btn.setText(closeButtonText);

        close_btn.setOnClickListener(closeButton);

        if(contentsColor != null){
            messages.setTextColor(Color.parseColor(contentsColor));
            close_btn.setTextColor(Color.parseColor(contentsColor));
        }
    }

    public CustomDialogOneType(@NonNull Context context,
                               String title,
                               String contents,
                               String closeButtonText,
                               View.OnClickListener closeButton){
        super(context);

        this.title = title;
        this.contents = contents;
        this.closeButtonText = closeButtonText;
        this.closeButton = closeButton;
    }

    public CustomDialogOneType(@NonNull Context context,
                               String title,
                               String contents,
                               String closeButtonText,
                               View.OnClickListener closeButton,
                               String contentsColor){
        super(context);

        this.title = title;
        this.contents = contents;
        this.closeButtonText = closeButtonText;
        this.closeButton = closeButton;
        this.contentsColor = contentsColor;
    }
}
