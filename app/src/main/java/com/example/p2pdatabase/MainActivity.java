package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationService;
import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationServiceConnection;

public class MainActivity extends AppCompatActivity {

    NotificationServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, NotificationService.class);
        connection = new NotificationServiceConnection();

        String title = "YEEEEEET!";
        String content = "BeepBop";
        int priority = 5;

        intent.putExtra("TITLE", title);
        intent.putExtra("CONTENT", content);
        intent.putExtra("PRIORITY", priority);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        //startService(new Intent(this, NotificationService.class));
    }

}
