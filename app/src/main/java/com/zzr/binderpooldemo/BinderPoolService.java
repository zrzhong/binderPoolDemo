package com.zzr.binderpooldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BinderPoolService extends Service {
    private static final String TAG = "BinderPoolService";
    private IBinder binder = new BinderPool.BinderPoolImpl();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
