package com.example.Android_WiFiAP;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;


public class MyWiFIAPManager {

    private WifiManager wifiManager;
    private String name_ap;
    private String ip_server = "192.168.43.1";
    private String key;
    private Server mServer;

    public MyWiFIAPManager(WifiManager _wifiManager, String _name_ap, String _key) {
        wifiManager = _wifiManager;
        name_ap = _name_ap;
        key = _key;
    }

    public void createWifiAccessPoint() throws SocketException {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        Log.d("Debug:getClass:", wifiManager.getClass().toString());
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
        boolean methodFound = false;
        for (Method method : wmMethods) {
            if (method.getName().equals("setWifiApEnabled")) {
                methodFound = true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = name_ap;


                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.preSharedKey = key;
                try {
                    boolean apstatus = (Boolean) method.invoke(wifiManager, netConfig, true);
                    for (Method isWifiApEnabledmethod : wmMethods) {
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {
                            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
                            }
                            ;
                            for (Method method1 : wmMethods) {
                                if (method1.getName().equals("getWifiApState")) {
                                    int apstate;
                                    apstate = (Integer) method1.invoke(wifiManager);
                                    Log.d("Debug:my", String.valueOf(apstate));
                                }
                            }
                        }
                    }
                    if (apstatus) {
                        Log.d("Splash Activity", "Access Point created");
                    } else {
                        Log.d("Splash Activity", "Access Point creation failed");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!methodFound) {
            Log.d("Splash Activity", "cannot configure an access point");
        }
    }

    public void start(Handler handler) {
        mServer = new Server(handler);
    }

    public void exit() {

        mServer.interruptAll();
    }

    public AbstractClient getIpClient() {
        AbstractClient client = mServer.getClient();
        return client;
    }

    public Socket getSocketClient() {
        return mServer.getClient().getSocket();
    }
//    public void start_client_handler() {
//        MyTask server = new MyTask();
//        server.execute();
//    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }
}
