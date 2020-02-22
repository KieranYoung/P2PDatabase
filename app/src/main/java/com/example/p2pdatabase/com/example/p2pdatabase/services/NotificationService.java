package com.example.p2pdatabase.com.example.p2pdatabase.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.p2pdatabase.R;

public class NotificationService extends Service {

    private int notificationId;
    private final IBinder mBinder = new NotificationServiceBinder();

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        setupP2PNotificationChannel();
        super.onCreate();
    }

    public class NotificationServiceBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");
        int priority = intent.getIntExtra("PRIORITY", 4);

        if(title == null){
            throw new RuntimeException("No Notification Title Set!");
        } else if (content == null){
            throw new RuntimeException("No Notification Content Set!");
        }

        postMessage(title, content, priority);
        notificationId++;

        return mBinder;
    }

    private void postMessage(String title, String content, int priority){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(priority);
        notificationManager.notify(notificationId, builder.build());
    }

    private void setupP2PNotificationChannel(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.NOTIFICATION_CHNL_ID);
            String description = getString(R.string.NOTIFICATION_CHNL_DESCRIP);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.NOTIFICATION_CHNL_ID), name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
