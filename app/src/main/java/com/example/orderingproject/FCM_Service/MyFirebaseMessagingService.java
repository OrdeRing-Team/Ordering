package com.example.orderingproject.FCM_Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.orderingproject.ENUM_CLASS.OrderType;
import com.example.orderingproject.R;
import com.example.orderingproject.MainActivity;
import com.example.orderingproject.AuthActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final String PACKING_CHANNEL = "PACKING";
    final String TABLE_CHANNEL = "TABLE";
    final String WAITING_CHANNEL = "WAITING";

    @Override
    public void onNewToken(String p0) {
        super.onNewToken(p0);
    }

    public void showDataMessage(String msgTitle, String msgContent) {
        Log.i("### data msgTitle : ", msgTitle);
        Log.i("### data msgContent : ", msgContent);
        String toastText = String.format("[Data 메시지] title: %s => content: %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * 수신받은 메시지를 Toast로 보여줌
     * @param msgTitle
     * @param msgContent
     */
    public void showNotificationMessage(String msgTitle, String msgContent) {
        Log.i("### noti msgTitle : ", msgTitle);
        Log.i("### noti msgContent : ", msgContent);
        String toastText = String.format("[Notification 메시지] title: %s => content: %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * 메시지 수신받는 메소드
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Intent intent = new Intent(this, AuthActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Uri sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        String channel = remoteMessage.getData().get("channel_id");


        Log.e("//=======//","===================================//");
        Log.e("   Title  :" , title);
        Log.e("//=======//","===================================//");

        Log.e("//=======//","===================================//");
        Log.e("   body   :" , message);
        Log.e("//=======//","===================================//");

        Log.e("//=======//","===================================//");
        Log.e("   channel   :" , channel);
        Log.e("//=======//","===================================//");

//        switch (channel){
//            case "ORDER":
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_order");
//                break;
//            case "TAKEOUT":
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");
//                break;
//            case "WAITING":
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_waiting");
//                break;
//        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_DESCRIPTION = "ChannerDescription";
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
//            if(remoteMessage.getData().get("Channel_id").equals("Channel_Waiting")){
//                Channel = "Channel_Waiting";
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_waiting");
//            }else{
//                Channel = "CHANNELTAKEOUT";
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");
//            }
            Log.e("//=======//","===================================//");
            Log.e("   Sound  :", sound.toString());
            Log.e("//=======//","===================================//");

            // add in API level 26

            if(channel.equals(TABLE_CHANNEL)) {
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , TABLE_CHANNEL);
                Log.e("//=======//","===================================//");
//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_order");

                NotificationChannel mChannel = new NotificationChannel("TABLE", "매장주문 알림", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }else if(channel.equals(PACKING_CHANNEL)){
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , PACKING_CHANNEL);
                Log.e("//=======//","===================================//");

//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_takeout");

                NotificationChannel mChannel = new NotificationChannel("PACKING", "포장주문 알림", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }
            else if(channel.equals(WAITING_CHANNEL)) {
                Log.e("//=======//","===================================//");
                Log.e("CHANNELT TYPE :" , WAITING_CHANNEL);
                Log.e("//=======//","===================================//");

//                sound = Uri.parse("android.resource://com.example.orderingmanager/raw/notify_waiting");

                NotificationChannel mChannel = new NotificationChannel("WAITING", "웨이팅 알림", importance);
                mChannel.setDescription(CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//                mChannel.setSound(sound, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notify_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setSound(sound)
                .setContentIntent(pendingIntent);


        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setContentTitle(title);
            builder.setVibrate(new long[]{500, 500});
        }
//        notificationManager.notify(Integer.parseInt(remoteMessage.getData().get("Type")), builder.build());
        notificationManager.notify((int)System.currentTimeMillis()/1000, builder.build());

    }

    private RemoteViews getCustomDesign(String title, String message) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewText(R.id.noti_message, message);
        remoteViews.setImageViewResource(R.id.logo, R.drawable.icon);
        return remoteViews;
    }

    public void showNotification(String title, String message) {
        //팝업 터치시 이동할 액티비티를 지정합니다.
        Intent intent = new Intent(this, MainActivity.class);
        //알림 채널 아이디 : 본인 하고싶으신대로...
        String channel_id = "CHN_ID";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //기본 사운드로 알림음 설정. 커스텀하려면 소리 파일의 uri 입력
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify_order);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000}) //알림시 진동 설정 : 1초 진동, 1초 쉬고, 1초 진동
                .setOnlyAlertOnce(true) //동일한 알림은 한번만.. : 확인 하면 다시 울림
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) { //안드로이드 버전이 커스텀 알림을 불러올 수 있는 버전이면
            //커스텀 레이아웃 호출
            builder = builder.setContent(getCustomDesign(title, message));
        } else { //아니면 기본 레이아웃 호출
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.icon); //커스텀 레이아웃에 사용된 로고 파일과 동일하게..
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //알림 채널이 필요한 안드로이드 버전을 위한 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "CHN_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //알림 표시 !
        notificationManager.notify(0, builder.build());
    }
}
