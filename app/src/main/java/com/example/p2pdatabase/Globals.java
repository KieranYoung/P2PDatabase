package com.example.p2pdatabase;

import android.app.Application;

import sqlite.SQL;

public class Globals extends Application{
    public static ConnectionClient CClient;

    enum Mode
    {
        LookingAndListening, Updating
    }

    private static Mode mode;

    public static void setMode(Mode m) {
        mode = m;
    }

    public static Mode getMode() {
        return mode;
    }

    public static SQL sql;

    public static long android_id;
}
