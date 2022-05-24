package com.example.orderingproject;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class Utillity {
    public Utillity(){/* */}

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
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
}
