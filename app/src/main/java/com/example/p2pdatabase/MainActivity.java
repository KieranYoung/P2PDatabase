package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.provider.Settings.Secure;
import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationService;

import androidx.core.app.ActivityCompat;
import compression.Compress;
import sqlite.SQL;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 168876;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102421;
    Button recieveButton;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                42609);


        Globals.android_id = Long.parseLong(Secure.getString(MainActivity.this.getContentResolver(),
                Secure.ANDROID_ID).substring(0, 14), 16);
        Globals.sql = new SQL(Globals.android_id, MainActivity.this);

        Globals.CClient.setContext(MainActivity.this);
        Globals.CClient.startDiscovery();
        Globals.CClient.startAdvertise();


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

        }


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

        }



        Compress.createDirectories();

        recieveButton = findViewById(R.id.recv);
        sendButton = findViewById(R.id.send);

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
                //stop looking and sending while we change contexts
                Globals.CClient.stopDiscovery();
                Globals.CClient.stopAdvert();
                Globals.CClient.disconnect();

                Intent myIntent = new Intent(MainActivity.this, Recv.class);
                startActivity(myIntent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this, Sender.class);
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
