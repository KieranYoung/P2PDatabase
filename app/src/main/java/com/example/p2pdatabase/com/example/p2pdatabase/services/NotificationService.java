package com.example.p2pdatabase.com.example.p2pdatabase.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.p2pdatabase.R;

import java.util.Random;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("Notification Service");
    }

    public NotificationService(String name) {
        super(name);
    }

    public static Intent setupNotificationWithNoProgressBar(String title, String content, int priority, int notificationId, Context context){
        Intent intent = new Intent();
        intent.putExtra("NOTIFICATION_ID", new Random().nextInt());
        intent.putExtra("TITLE", title);
        intent.putExtra("CONTENT", content);
        intent.putExtra("PRIORITY", priority);
        intent.putExtra("TRANSFER_PROG", -1);
        return intent;
    }

    public static Intent updateProgressBarNotification(int notificationId, String title, String content, int priority, int progress, Context context){
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("TITLE", title);
        intent.putExtra("CONTENT", content);
        intent.putExtra("PRIORITY", priority);
        intent.putExtra("TRANSFER_PROG", progress);

        return intent;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        postMessage(intent);
    }

    private void postMessage(Intent intent) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        int notificationId = (intent.getIntExtra("NOTIFICATION_ID", -1));
        if (intent.getIntExtra("TRANSFER_PROG", -1) == -1) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(intent.getStringExtra("TITLE"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setPriority(intent.getIntExtra("PRIORITY", 4));
            notificationManager.notify(notificationId, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(intent.getStringExtra("TITLE"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setPriority(intent.getIntExtra("PRIORITY", 4))
                    .setProgress(100, intent.getIntExtra("TRANSFER_PROG", 0), false);
            notificationManager.notify(notificationId, builder.build());
        }

    }
}
