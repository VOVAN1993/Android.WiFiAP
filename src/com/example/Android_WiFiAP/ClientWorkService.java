package com.example.Android_WiFiAP;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by root on 16.01.14.
 */
public class ClientWorkService extends Service {
    private Client client;
    private BroadcastReceiver receiverKillMySelf;
    public static final String BROADCAST_KILL_MYSELF = "com.example.AndroidWiFi-AP.killmyself";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ClientActivity.LOG_CLIENT, "Service OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId > 1) {
            Log.d(ClientActivity.LOG_CLIENT, "StartId is not valid");
            return super.onStartCommand(intent, flags, startId);
        }
        Log.d(ClientActivity.LOG_CLIENT, "Service onStartCommand");
//        Intent intent_for_chat = new Intent(ClientActivity.BROADCAST_CHAT);
//        intent_for_chat.putExtra(ClientActivity.PARAM_MESS,"Connect OK");
//        getApplicationContext().sendBroadcast(intent_for_chat);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new Client(null, getApplicationContext());
                client.startWork();
            }
        }).start();

        receiverKillMySelf = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                client.killSelf();
                stopSelf();
            }
        };
        IntentFilter intFiltForBroadcast = new IntentFilter(BROADCAST_KILL_MYSELF);
        registerReceiver(receiverKillMySelf, intFiltForBroadcast);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        client.killSelf();
//        Log.d(ClientActivity.LOG_CLIENT,"Service OnDestroy");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
