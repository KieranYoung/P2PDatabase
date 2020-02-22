package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationService;

import compression.Compress;

public class MainActivity extends AppCompatActivity {
    Button recieveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Compress.createDirectories();

        recieveButton = findViewById(R.id.recv);

        setupP2PNotificationChannel();

        String title = "YEEEEEET!";
        String content = "BeepBop";
        int priority = 5;

        for(int i = 1; i <= 100; i++){
            System.out.println("AIJSDGHOUASGDUKHGASVGFDGHYSAVDGHY ASDGJFH DGFHVCAGFHJSDASJGHFDCVSAGFHJDCVASGFJDCASFCDGSFADCSAFGD " + i);
            Intent intent = NotificationService.setupNotificationWithProgressBar(title, content, priority, (i) ,1, this);
            startService(intent);
        }

        recieveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this, Recv.class);
                startActivity(myIntent);
            }
        });

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
