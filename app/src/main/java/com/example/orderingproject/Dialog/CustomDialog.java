package com.example.orderingproject.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.orderingproject.databinding.CustomDialogBinding;

public class CustomDialog extends Dialog {
    private CustomDialogBinding binding;
    private View.OnClickListener positiveButton;
    private View.OnClickListener negativeButton;

    String title;
    String contents;
    String positiveButtonText;
    String negativeButtonText;
    TextView messages;
    String contentsColor;

    Long foodId;
    int totalPrice, totalCount;
    int absolutePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView titleText = binding.tvTitle;
        messages = binding.tvMessage;
        Button positive_btn = binding.positiveBtn;
        Button negative_btn = binding.negativeBtn;

        titleText.setText(title);
        messages.setText(contents);
        positive_btn.setText(positiveButtonText);
        negative_btn.setText(negativeButtonText);

        positive_btn.setOnClickListener(positiveButton);
        negative_btn.setOnClickListener(negativeButton);

        if(contentsColor != null){
            messages.setTextColor(Color.parseColor(contentsColor));
            positive_btn.setTextColor(Color.parseColor(contentsColor));
        }
    }

    public CustomDialog(@NonNull Context context,
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
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
    }
    public CustomDialog(@NonNull Context context,
                        String title,
                        String contents,
                        String positiveButtonText,
                        String negativeButtonText,
                        View.OnClickListener positiveButton,
                        View.OnClickListener negativeButton, String contentsColor,
                        Long foodId, int totalPrice, int totalCount){
        super(context);

        this.title = title;
        this.contents = contents;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.contentsColor = contentsColor;
        this.foodId = foodId;
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
    }
    public CustomDialog(@NonNull Context context,
                        String title,
                        String contents,
                        String positiveButtonText,
                        String negativeButtonText,
                        View.OnClickListener positiveButton,
                        View.OnClickListener negativeButton,
                        String contentsColor){
        super(context);

        this.title = title;
        this.contents = contents;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.contentsColor = contentsColor;
    }

    public CustomDialog(@NonNull Context context,
                        String title,
                        String contents,
                        String positiveButtonText,
                        String negativeButtonText,
                        View.OnClickListener positiveButton,
                        View.OnClickListener negativeButton,
                        String contentsColor, int absolutePosition){
        super(context);

        this.title = title;
        this.contents = contents;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.contentsColor = contentsColor;
        this.absolutePosition = absolutePosition;
    }

    public void showProgress(){
        binding.progressBar.setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopProgress(){
        binding.progressBar.setVisibility(View.GONE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public int getAbsolutePosition(){
        return absolutePosition;
    }

    public Long getFoodId(){
        return foodId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
