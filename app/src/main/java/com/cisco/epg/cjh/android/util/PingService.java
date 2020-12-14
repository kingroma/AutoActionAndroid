package com.cisco.epg.cjh.android.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class PingService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("pingService");

        return null;
    }
}
