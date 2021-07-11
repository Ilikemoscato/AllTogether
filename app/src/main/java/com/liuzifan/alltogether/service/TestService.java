package com.liuzifan.alltogether.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TestService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /**
         * 返回值:
         * START_NOT_STICKY 表示当Service运行的进程被Android系统强制杀掉之后，
         *                   不会重新创建该Service，当然如果在其被杀掉之后一段时
         *                   间又调用了startService，那么该Service又将被实例化
         *
         * START_STICKY     Service运行的进程被Android系统强制杀掉之后，Android
         *                  系统会将该Service依然设置为started状态（即运行状态），
         *                  但是不再保存onStartCommand方法传入的intent对象，然后
         *                  Android系统会尝试再次重新创建该Service，并执行
         *                  onStartCommand回调方法，但是onStartCommand回调方法
         *                  的Intent参数为null，也就是onStartCommand方法虽然会
         *                  执行但是获取不到intent信息。
         *
         * START_REDELIVER_INTENT   表示Service运行的进程被Android系统强制杀掉
         *                          之后，与返回START_STICKY的情况类似，Android
         *                          系统会将再次重新创建该Service，并执行
         *                          onStartCommand回调方法，但是不同的是，
         *                          Android系统会再次将Service在被杀掉之前最
         *                          后一次传入onStartCommand方法中的Intent再次
         *                          保留下来并再次传入到重新创建后的Service的onStartCommand
         *                          方法中，这样我们就能读取到intent参数
         */
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
