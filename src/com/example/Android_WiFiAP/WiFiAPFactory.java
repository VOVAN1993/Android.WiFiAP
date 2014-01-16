package com.example.Android_WiFiAP;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.SocketException;
import java.util.concurrent.Callable;

/**
 * Created by root on 13.01.14.
 */
public class WiFiAPFactory implements Callable<MyWiFIAPManager> {
    private static final long serialVersionUID = 1L;
    private final MyWiFIAPManager myWiFIAPManager;
    private final Context context;
    private final String name;
    private final String pass;

    public WiFiAPFactory(Context _context, String _name, String _pass) {
        context = _context;
        name = _name;
        pass = _pass;
        myWiFIAPManager = new MyWiFIAPManager((WifiManager) context.getSystemService(Context.WIFI_SERVICE), name, pass);
    }

    @Override
    public MyWiFIAPManager call() throws Exception {
        try {
            Log.d(Server_Activity.LOG_D, "Create AP:Begin");
            myWiFIAPManager.createWifiAccessPoint();
            // Thread.currentThread().sleep(10000);
            Log.d(Server_Activity.LOG_D, "Create AP:End");
            return myWiFIAPManager;
        } catch (SocketException e) {
            Log.d(Server_Activity.LOG_D, "Error when creating an AP");
            e.printStackTrace();
        }
        return null;
    }
}
