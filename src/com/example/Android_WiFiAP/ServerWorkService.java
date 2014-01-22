package com.example.Android_WiFiAP;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.example.Android_WiFiAP.Utils.Util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by root on 14.01.14.
 */
public class ServerWorkService extends Service {
    public static final String LOG_SERVER_WORK = "Debug:ServerWork";
    private MyWiFIAPManager wifiAPManager;
    private ExecutorService pool;
    private BroadcastReceiver receiverForCountClient;
    public static final String BROADCAST_COUNT_CLIENTS = "com.example.Android_WiFiAP.count_clients";

    public ServerWorkService() {
        pool = Executors.newFixedThreadPool(3);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_SERVER_WORK, "OnCreate Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId > 1) {
            Log.d(LOG_SERVER_WORK, "StartId is not valid");
            super.onStartCommand(intent, flags, startId);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Future<MyWiFIAPManager> futureCreateAP = pool.submit(new WiFiAPFactory(getApplicationContext(), "vova", "12345678"));
                try {
                    wifiAPManager = futureCreateAP.get();
                } catch (InterruptedException e) {
                    Log.d(LOG_SERVER_WORK, e.getMessage());
                } catch (ExecutionException e) {
                    Log.d(LOG_SERVER_WORK, e.getMessage());
                }
                Log.d(LOG_SERVER_WORK, "WiFiManager is created");
                Log.d(LOG_SERVER_WORK, "My Server " + Util.getLocalIpAddressString());
                wifiAPManager.start(null, getApplicationContext());
                Intent enabledExitButton = new Intent(Server_Activity.BROADCAST_SERVER_ACTIVITY);
                enabledExitButton.putExtra(Server_Activity.SERVER_TYPE, Server_Activity.TYPE_SERVER_EXIT_ENABLED);
                sendBroadcast(enabledExitButton);
            }
        }).start();

        receiverForCountClient = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(Server_Activity.LOG_D, "Count clients = " + wifiAPManager.getCountClients());
                Util.sendToTextViewServer("Count clients = " + wifiAPManager.getCountClients(), context);
            }
        };

        IntentFilter intFiltForBroadcast = new IntentFilter(BROADCAST_COUNT_CLIENTS);
        registerReceiver(receiverForCountClient, intFiltForBroadcast);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_SERVER_WORK, "OnDestroy Service");
        wifiAPManager.exit();
        wifiAPManager.stopAP();
        unregisterReceiver(receiverForCountClient);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
