package com.example.orderingproject;

import android.app.Activity;
import android.widget.Toast;

public class Utillity {
    public Utillity(){/* */}

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

}
