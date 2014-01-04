package com.example.Android_WiFiAP;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.SocketException;
import java.util.ArrayList;

public class Server_Activity extends Activity {
    public static final String LOG_D = "Debug:Server_act";
    TextView textView1;
    MyWiFIAPManager wifiManager;
    BroadcastReceiver br;
    Button button;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(LOG_D, "Start ServerActivity");
        textView1 = (TextView) findViewById(R.id.n);
        button = (Button) findViewById(R.id.button1);
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

        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                Log.d("Debug:info", "!!!!!!!!!" + intent.getAction());
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intFilt.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intFilt.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intFilt.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intFilt.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intFilt.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intFilt.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
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

    public void onClickButton(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        break;
//        Log.d(LOG_D_BUTTON, "tut" + v.toString());
//        textView1.setText("");
//        scan();
    }
}