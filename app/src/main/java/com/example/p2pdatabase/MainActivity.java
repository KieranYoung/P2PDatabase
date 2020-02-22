package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupP2PNotificationChannel();

        String title = "YEEEEEET!";
        String content = "BeepBop";
        int priority = 5;

        for(int i = 1; i <= 100; i++){
            System.out.println("AIJSDGHOUASGDUKHGASVGFDGHYSAVDGHY ASDGJFH DGFHVCAGFHJSDASJGHFDCVSAGFHJDCVASGFJDCASFCDGSFADCSAFGD " + i);
            Intent intent = NotificationService.setupNotificationWithProgressBar(title, content, priority, (i) ,1, this);
            startService(intent);
        }

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
