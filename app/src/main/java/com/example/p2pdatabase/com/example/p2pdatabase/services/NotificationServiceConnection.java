package com.example.p2pdatabase.com.example.p2pdatabase.services;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class NotificationServiceConnection implements ServiceConnection {

    NotificationService mService;
    boolean mBound = false;

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        NotificationService.NotificationServiceBinder binder = (NotificationService.NotificationServiceBinder) service;
        mService = binder.getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mBound = false;
    }

}
