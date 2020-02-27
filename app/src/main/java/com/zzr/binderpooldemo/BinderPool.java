package com.zzr.binderpooldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.CountDownLatch;

public class BinderPool {
    private static final String TAG = "BinderPool";
    public static final int BINDER_NONE = -1;
    public static final int BINDER_SECURITY = 0;
    public static final int BINDER_COMPUTE = 1;
    private CountDownLatch countDownLatch;
    private IBinderPool iBinderPool;
    private Context context;
    private static volatile BinderPool binderPool;

    private BinderPool(Context context) {
        this.context = context.getApplicationContext();
        //连接远程服务
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (binderPool == null) {
            synchronized (BinderPool.class) {
                if (binderPool == null) {
                    binderPool = new BinderPool(context);
                }
            }
        }
        return binderPool;
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (iBinderPool != null) {
            try {
                binder = iBinderPool.query(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    private void connectBinderPoolService() {
        countDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(context, BinderPoolService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                iBinderPool.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Binder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //binder死亡时回调
            iBinderPool.asBinder().unlinkToDeath(deathRecipient, 0);
            iBinderPool = null;
            //重现连接远程服务
            connectBinderPoolService();
        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub {

        @Override
        public IBinder query(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_SECURITY:
                    binder = new ScurityCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
            }
            return binder;
        }
    }
}
