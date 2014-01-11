package com.example.Android_WiFiAP;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server_Activity extends Activity {
    public static final String LOG_D = "Debug:Server_act";
    public static final String LOG_D_FOR_ACT = "Debug:Activitys";
    TextView textView1;
    MyWiFIAPManager wifiManager;
    private BroadcastReceiver br;
    Button button, exitButton;
    private boolean isRotate;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        isRotate = false;
        wifiManager = null;
        if (savedInstanceState != null) {
            isRotate = savedInstanceState.getBoolean("isRotate");
            wifiManager = (MyWiFIAPManager) getLastNonConfigurationInstance();
        }
        Log.d(LOG_D, "Start ServerActivity");
        Log.d(LOG_D_FOR_ACT, "ServerActivity.onCreate");
        textView1 = (TextView) findViewById(R.id.chat);
        button = (Button) findViewById(R.id.button1);
        exitButton = (Button) findViewById(R.id.exit_button);
        boolean is_server = true;
        if (is_server && !isRotate) {
            wifiManager = new MyWiFIAPManager((WifiManager) getSystemService(Context.WIFI_SERVICE), "vova", "12345678");
            try {
                wifiManager.createWifiAccessPoint();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                textView1.append(chatLine);
                textView1.append("\n");
            }
        };
        h.sendMessage(Util.getMessageFromString("My Server " + Util.getLocalIpAddressString(), "msg"));
        Log.d(LOG_D, Util.getLocalIpAddressString());
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("wifi_state", 0) == 10) {
                    Log.d(Server.LOG_D, "Try interruptAll");
                    wifiManager.exit();
                }
                Log.d("Debug:info", "!!!!!!!!!" + intent.getAction());
            }
        };
        IntentFilter intFilt = new IntentFilter();
        intFilt.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(br, intFilt);

        if (!isRotate) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    wifiManager.start(h);
                }
            }).start();

        }
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

    @Override
    protected void onStart() {
        Log.d(LOG_D_FOR_ACT, "Server.onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_D_FOR_ACT, "Server.onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_D_FOR_ACT, "Server.onPause");
    }

    @Override
    protected void onStop() {
        Log.d(LOG_D_FOR_ACT, "Server.onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        Log.d(LOG_D_FOR_ACT, "Server.onDestroy");
    }


    @Override
    public Object onRetainNonConfigurationInstance() {
        return wifiManager;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRotate", true);
    }

    public void onClickButton(View v) throws IOException {
        switch (v.getId()) {
            case R.id.exit_button:
                Log.d(Server.LOG_D, "Begin \"The end\" ");
                wifiManager.exit();
                finish();
                //TODO: сделать exit  в другом потоке
                break;
            case R.id.button1:
                Log.d(LOG_D, "Try ping:");
                InetAddress ip = wifiManager.getIpClient().getIPAddress();
                Log.d(LOG_D, "IP client = " + ip.toString());
                String ret = Util.ping(ip.toString());
                Log.d(LOG_D, ret);
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
                break;
            case R.id.ping_button:
                Log.d(LOG_D, "Try ping over socket");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = wifiManager.getSocketClient();
                        Util.ping_over_socket(wifiManager.getSocketClient(), 10);
                    }
                }).start();

            default:
                Log.d(LOG_D, "Unknown button");
                break;
        }

//        break;
//        Log.d(LOG_D_BUTTON, "tut" + v.toString());
//        textView1.setText("");
//        scan();
    }
}