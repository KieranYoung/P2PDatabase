package com.example.p2pdatabase.com.example.p2pdatabase.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.p2pdatabase.R;

import java.util.Random;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("notification");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Intent setupNotificationWithNoProgressBar(String title, String content, int priority, Context context){

        Intent intent = new Intent(context, NotificationService.class);

        intent.putExtra("TITLE", title);
        intent.putExtra("CONTENT", content);
        intent.putExtra("PRIORITY", priority);
        intent.putExtra("TRANSFER_PROG", -1);

        return intent;
    }

    public static Intent setupNotificationWithProgressBar(String title, String content, int priority, int percentage, int notificationId, Context context){

        Intent intent = new Intent(context, NotificationService.class);

        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("TITLE", title);
        intent.putExtra("CONTENT", content);
        intent.putExtra("PRIORITY", priority);
        intent.putExtra("TRANSFER_PROG", percentage);

        return intent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        postMessage(intent);
    }

    private void postMessage(Intent intent){

        if(intent.getIntExtra("TRANSFER_PROG", -1) != -1){
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(intent.getStringExtra("TITLE"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setPriority(intent.getIntExtra("PRIORITY", 4))
                    .setProgress(100, intent.getIntExtra("TRANSFER_PROG", 0), false);
            notificationManager.notify(intent.getIntExtra("NOTIFICATION_ID", 0), builder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(intent.getStringExtra("TITLE"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setPriority(intent.getIntExtra("PRIORITY", 4));
            notificationManager.notify(new Random().nextInt(), builder.build());
        }
    }

}
