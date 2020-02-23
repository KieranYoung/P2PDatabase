package com.example.p2pdatabase.com.example.p2pdatabase.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.p2pdatabase.R;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationService extends Service {

    private ConcurrentHashMap<Integer, NotificationCompat.Builder> progressBarNotifications;
    private final IBinder binder = new NotifBinder();


    public NotificationService() {
        progressBarNotifications = new ConcurrentHashMap<>();
    }

    public static int generateRandomNotificationId(){
        return new Random().nextInt();
    }

    public static Intent setupNotificationWithNoProgressBar(String title, String content, int priority, int notificationId, Context context){

        Intent intent = new Intent(context, NotificationService.class);

        intent.putExtra("NOTIFICATION_ID", notificationId);
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

    public static Intent setupUpdateProgressBar(int notificationId, Context context){
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("TRANSFER_PROG", 0);
        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        postMessage(intent);
        System.out.println(intent.getIntExtra("NOTIFICATION_ID", -1));
        return binder;
    }

    @Override
    public void onCreate() {
        setupP2PNotificationChannel();
        super.onCreate();
    }

    private void postMessage(Intent intent){
        Log.d("HERE HE WE BOYZ", "YOINK");
    /*
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        int notificationId = (intent.getIntExtra("NOTIFICATION_ID", -1));
        if(intent.getIntExtra("TRANSFER_PROG", -1) == -1){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(intent.getStringExtra("TITLE"))
                    .setContentText(intent.getStringExtra("CONTENT"))
                    .setPriority(intent.getIntExtra("PRIORITY", 4));
            notificationManager.notify(notificationId, builder.build());
        } else {


            NotificationCompat.Builder notificationGenerator;
            if(progressBarNotifications.containsKey(notificationId)){
                notificationGenerator = progressBarNotifications.get(notificationId);
                //notificationGenerator.setProgress(100, intent.getIntExtra("TRANSFER_PROG", 0), false);
                System.out.println("I AM SOUTY");
            } else {
                notificationGenerator = new NotificationCompat.Builder(this, this.getResources().getString(R.string.NOTIFICATION_CHNL_ID))
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(intent.getStringExtra("TITLE"))
                        .setContentText(intent.getStringExtra("CONTENT"))
                        .setPriority(intent.getIntExtra("PRIORITY", 4))
                        .setProgress(100, intent.getIntExtra("TRANSFER_PROG", 0), false);
                progressBarNotifications.put(notificationId, notificationGenerator);
            }
            notificationManager.notify(notificationId, notificationGenerator.build());

        }
        */

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

    public class NotifBinder extends Binder {
        NotificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NotificationService.this;
        }
    }


}
