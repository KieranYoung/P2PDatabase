package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.example.p2pdatabase.com.example.p2pdatabase.services.NotificationService;

import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import compression.Compress;
import sqlite.SQL;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 168876;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102421;
    Button recieveButton;
    Button sendButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                42609);


        Globals.android_id = Long.parseLong(Secure.getString(MainActivity.this.getContentResolver(),
                Secure.ANDROID_ID).substring(0, 14), 16);
        Globals.sql = new SQL(Globals.android_id, MainActivity.this);

        Globals.CClient = new ConnectionClient();
        Globals.CClient.setContext(MainActivity.this);




        ArrayList<File> files = Globals.sql.getFiles(Globals.android_id);
        for (File s: files) {
            InputStream is = null;
            OutputStream os = null;
            try {
                File file = new File(Compress.inPath + '/' + s.getName());
                is = new FileInputStream(s);
                os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer);
                }
                is.close();
                os.close();
            } catch (Exception e) {

            }
        }

        File temp = new File(Compress.inPath);
        System.out.println("About to brint doofus llooooook herereererererer");
        for (File f: temp.listFiles()) {
            System.out.println("hey");
            System.out.println(f.getName());
        }





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
        String content = "";
        int priority = 5;

        for(int i = 1; i <= 40; i++){
            content = "Download Completion: %" + i;
            Intent newIntentService = NotificationService.updateProgressBarNotification(1, title, content, priority, i, this);
            startService(newIntentService);
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
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 7);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 7:
                if(resultCode==RESULT_OK){
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = getContentResolver().openInputStream(data.getData());
                        String name = getFileName(data.getData());
                        File file = new File(Compress.inPath + '/' + name);
                        os = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer);
                        }
                        is.close();
                        os.close();
                    } catch (Exception e) {

                    }
                    String name = getFileName(data.getData());
                    File file = new File(Compress.inPath + '/' + name);
                    byte[] bytesArray = new byte[(int) file.length()];
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                    } catch (Exception e) {

                    }
                    Globals.sql.insertFile(bytesArray);
                    Compress.deleteFiles();
                }
                break;
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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
    @Override
    public void onStart(){
        super.onStart();
        Globals.CClient.startDiscovery();
        Globals.CClient.startAdvertise();
    }

}
