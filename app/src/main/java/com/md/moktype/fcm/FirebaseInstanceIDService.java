package com.md.moktype.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;

import com.md.moktype.R;
import com.md.moktype.common.Constants;
import com.md.moktype.ui.activity.SplashActivity;
import com.md.moktype.utils.Prefs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class FirebaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        Prefs.putString(Constants.PREFS_FCM_TOKEN, s);
        super.onNewToken(s);
    }

    /**
     * 메세지를 받았을 경우 그 메세지에 대하여 구현하는 부분입니다.
     * **/
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        badgeUpdate();
        sendNotification(remoteMessage);
    }

    public void badgeUpdate(){
        int badgeCount = Prefs.getInt(Constants.PREFS_BADGE_COUNT);
        badgeCount++;
        Prefs.putInt(Constants.PREFS_BADGE_COUNT, badgeCount);
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", badgeCount);
        badgeIntent.putExtra("badge_count_package_name", getPackageName());
        badgeIntent.putExtra("badge_count_class_name", getLauncherClassName());
        sendBroadcast(badgeIntent);
    }

    private String getLauncherClassName(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,0);
        for(ResolveInfo resolveInfo: resolveInfos){
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if(pkgName.equalsIgnoreCase(getPackageName())){
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    /**
     * remoteMessage 메세지 안에 getData와 getNotification이 있습니다.
     * 이부분은 차후 테스트 날릴때 설명 드리겠습니다.
     **/
    private void sendNotification(RemoteMessage remoteMessage) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent introIntent = new Intent(getApplicationContext(), SplashActivity.class);

//        HashMap<String, String> dataMap = new HashMap<>(remoteMessage.getData());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("PanBook", "PanBook", importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Notification.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, "PanBook");
        } else {
            builder = new Notification.Builder(this);
        }

        String strTitle = null;
        String strMessage = null;

        if( remoteMessage.getNotification() == null   ){
            strTitle = remoteMessage.getData().get("title");
            strMessage = remoteMessage.getData().get("body");
        } else{
            strTitle = remoteMessage.getNotification().getTitle();
            strMessage = remoteMessage.getNotification().getBody();
        }

        introIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, introIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.ic_launcher) // 아이콘 설정하지 않으면 오류남
                .setContentTitle(strTitle) // 제목 설정
                .setContentText(strMessage) // 내용 설정
                .setTicker(strMessage) // 상태바에 표시될 한줄 출력
                .setAutoCancel(true)
                .setContentIntent(intent);

        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        notificationManager.notify((int)System.currentTimeMillis(), builder.build());
    }
}