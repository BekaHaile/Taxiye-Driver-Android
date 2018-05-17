package com.fugu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fugu.utils.FuguLog;

/**
 * Created by bhavya on 10/07/17.
 */

public class FuguOnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FuguLog.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FuguLog.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        FuguLog.e("ClearFromRecentService", "END");
        //Code here
        stopSelf();
    }
}
