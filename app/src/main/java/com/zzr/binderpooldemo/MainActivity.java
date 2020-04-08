package com.zzr.binderpooldemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new WorkRun()).start();
        ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
        threadLocal.set(true);
        Log.i(TAG, "onCreate: "+threadLocal.get());
//        Looper
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return false;
            }
        });
//        handler.sendMessage()
    }

    private class WorkRun implements Runnable {

        @Override
        public void run() {
            doWork();
        }
    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder binder = binderPool.queryBinder(BinderPool.BINDER_SECURITY);
        ISecurityCenter securityCenter = ScurityCenterImpl.asInterface(binder);
        try {
            String str = securityCenter.encrypt("helloworld-安卓");
            Log.i(TAG, "s: " + str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder binder1 = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        ICompute compute = ComputeImpl.asInterface(binder1);
        try {
            int sum = compute.add(2, 6);
            Log.i(TAG, "sum: " + sum);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
