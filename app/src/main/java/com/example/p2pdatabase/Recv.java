package com.example.p2pdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import compression.Compress;

public class Recv extends AppCompatActivity {
    ArrayList<String> files;
    Button returnButton;
    ListView fileListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv);

        returnButton =  findViewById(R.id.returnButton);
        fileListView = findViewById(R.id.filesListView);
        files = new ArrayList<>();

        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                //head back to title screen of game
                Recv.this.finish();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        File dir = new File(Compress.inPath);
        for (File f: dir.listFiles()) {
            files.add(f.getName());
            System.out.println(f.getName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Recv.this, android.R.layout.simple_list_item_1, files);
        fileListView.setAdapter(arrayAdapter);
    }
}