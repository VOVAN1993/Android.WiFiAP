package com.example.Android_WiFiAP;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import java.net.SocketException;
import java.util.ArrayList;

public class MyActivity extends Activity {
    TextView textView1;
    MyWiFIAPManager wifiManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView1 = (TextView) findViewById(R.id.n);
        boolean is_server = true;
        wifiManager = null;
        if (is_server) {
            wifiManager = new MyWiFIAPManager((WifiManager) getSystemService(Context.WIFI_SERVICE), "vova", "12345678");
            try {
                wifiManager.createWifiAccessPoint();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        wifiManager.start_client_handler();

    }

    private void scan() {
        ArrayList<ClientScanResult> clients = Util.getClientList();

        textView1.append("Clients: \n");
        textView1.append(String.valueOf(clients.size()));
        for (ClientScanResult clientScanResult : clients) {
            textView1.append("####################\n");
            textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
            textView1.append("Device: " + clientScanResult.getDevice() + "\n");
            textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
        }
    }

}