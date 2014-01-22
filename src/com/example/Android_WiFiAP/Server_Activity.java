package com.example.Android_WiFiAP;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.Android_WiFiAP.Utils.Util;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Server_Activity extends Activity {
    public static final String LOG_D = "Debug:Server_act";
    public static final String LOG_D_FOR_ACT = "Debug:Activitys";
    TextView textView1;
    MyWiFIAPManager wifiManager;
    private BroadcastReceiver receiverForUpdateChat;
    private BroadcastReceiver receiverForWiFi;
    Button button, exitButton;
    private EditText editText;
    public static final String BROADCAST_SERVER_ACTIVITY = "com.example.Android_WiFiAP";
    public static final String PARAM_MESS = "mess";

    public static final String SERVER_TYPE = "com.example.Android_WiFiAP.Server_type";
    public static final String TYPE_SERVER_UPDATE_TEXTVIEW = "com.example.Android_WiFiAP.UpdateTextView";
    public static final String TYPE_SERVER_EXIT_ENABLED = "com.example.Android_WiFiAP.enabledExitButton";

    private Intent intentServiceServerWork;

    private void registerBroadcastReceivers() {
        receiverForUpdateChat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra(SERVER_TYPE).equals(TYPE_SERVER_UPDATE_TEXTVIEW)) {
                    String mess = intent.getStringExtra(PARAM_MESS);
                    textView1.append(mess + '\n');
                    return;
                }
                if (intent.getStringExtra(SERVER_TYPE).equals(TYPE_SERVER_EXIT_ENABLED)) {
                    exitButton.setEnabled(true);
                    return;
                }
            }
        };
        IntentFilter intFiltForBroadcast = new IntentFilter(BROADCAST_SERVER_ACTIVITY);
        registerReceiver(receiverForUpdateChat, intFiltForBroadcast);

        receiverForWiFi = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                Log.d("Debug:info", "!!!!!!!!!" + intent.getAction());
                if (intent.getIntExtra("wifi_state", 0) == 10) {
                    stopService(intentServiceServerWork);
                    finish();
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter();
        intFilt.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");

        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(receiverForWiFi, intFilt);
    }

    private void unregisterBroadcastReceivers() {
        unregisterReceiver(receiverForUpdateChat);
        unregisterReceiver(receiverForWiFi);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        boolean isRotate = false;
        button = (Button) findViewById(R.id.button1);
        exitButton = (Button) findViewById(R.id.exit_button);
        textView1 = (TextView) findViewById(R.id.chat);
        editText = (EditText) findViewById(R.id.editText);
        if (savedInstanceState != null) {
            isRotate = savedInstanceState.getBoolean("isRotate");
//            isRotate=true;
        }
        if (!isRotate) {
            intentServiceServerWork = new Intent(this, ServerWorkService.class);
            startService(intentServiceServerWork);

        } else {
            exitButton.setEnabled(true);
        }
        registerBroadcastReceivers();
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
        unregisterBroadcastReceivers();
        Log.d(LOG_D_FOR_ACT, "Server.onDestroy");
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
                stopService(new Intent(this, ServerWorkService.class));
                //      wifiManager.exit();
                finish();
                //TODO: сделать exit  в другом потоке
                break;
            case R.id.button1:
//                Log.d(LOG_D, "Try ping:");
//                InetAddress ip = wifiManager.getIpClient().getIPAddress();
//                Log.d(LOG_D, "IP client = " + ip.toString());
//                String ret = Util.ping(ip.toString());
//                Log.d(LOG_D, ret);

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
                break;
            case R.id.countClients:
                Intent intent_for_count_clients = new Intent(ServerWorkService.BROADCAST_COUNT_CLIENTS);
                sendBroadcast(intent_for_count_clients);
                break;
            case R.id.send_button:
                Log.d(LOG_D, "Press send");
                String text = editText.getText().toString();
                if (text.trim().length() == 0) {
                    Log.d(LOG_D, "Text is empty");
                } else {
                    editText.setText("");
                    Intent intent_for_send_clients = new Intent(Server.BROADCAST_Server_FOR_QUEUE);
                    intent_for_send_clients.putExtra(Server.PARAM_MESS_QUEUE, text);
                    sendBroadcast(intent_for_send_clients);
                }

                break;
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