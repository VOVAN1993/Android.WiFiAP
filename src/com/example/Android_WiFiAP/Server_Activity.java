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
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class Server_Activity extends Activity {
    public static final String LOG_D = "Debug:Server_act";
    public static final String LOG_D_FOR_ACT = "Debug:Activitys";
    TextView textView1;
    MyWiFIAPManager wifiManager;
    private BroadcastReceiver br;
    private BroadcastReceiver receiverForUpdateChat;
    private BroadcastReceiver receiverEnabledExitButton;
    Button button, exitButton;
    private EditText editText;
    Map<String, Object> mSaveObjects;
    public static final String BROADCAST_TEXT = "com.example.Android_WiFiAP";
    public static final String BROADCAST_ENABLED = "com.example.Android_WiFiAP.enabledExitButton";
    public static final String PARAM_MESS = "mess";

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
            Intent intentServiceServerWork = new Intent(this, ServerWorkService.class);
            startService(intentServiceServerWork);

        } else {
            exitButton.setEnabled(true);
        }

        receiverForUpdateChat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mess = intent.getStringExtra(PARAM_MESS);
                textView1.append(mess + '\n');
            }
        };

        receiverEnabledExitButton = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exitButton.setEnabled(true);
            }
        };

        IntentFilter intFiltForBroadcast = new IntentFilter(BROADCAST_TEXT);
        registerReceiver(receiverForUpdateChat, intFiltForBroadcast);

        IntentFilter intFiltForBroadcastexitButton = new IntentFilter(BROADCAST_ENABLED);
        registerReceiver(receiverEnabledExitButton, intFiltForBroadcastexitButton);
//        isRotate = false;
//        wifiManager = null;
//        pool = Executors.newCachedThreadPool();
//        if (savedInstanceState != null) {
//            isRotate = savedInstanceState.getBoolean("isRotate");
//            mSaveObjects = (Map<String,Object>)getLastNonConfigurationInstance();
//            if(!(mSaveObjects.containsKey("wifiManager") &&
//                    mSaveObjects.containsKey("text")&&
//                    mSaveObjects.containsKey("handler")
//                )){
//                Log.d(LOG_D,"ERROR: map not contains something");
//            }
//            text = (StringBuilder)mSaveObjects.get("text");
//            wifiManager = (MyWiFIAPManager) mSaveObjects.get("wifiManager");
//            h = (Handler) mSaveObjects.get("handler");
//            textView1 = (TextView) mSaveObjects.get("textView1");
//            // wifiManager = (MyWiFIAPManager) getLastNonConfigurationInstance();
//        }
//        Log.d(LOG_D, "Start ServerActivity");
//        Log.d(LOG_D_FOR_ACT, "ServerActivity.onCreate");
//        button = (Button) findViewById(R.id.button1);
//        exitButton = (Button) findViewById(R.id.exit_button);
//
//
//        boolean is_server = true;
//        Future<MyWiFIAPManager> futureCreateAP=null;
//        if (is_server && !isRotate) {
//            textView1 = (TextView) findViewById(R.id.chat);
//            text = new StringBuilder();
//            futureCreateAP = pool.submit(new WiFiAPFactory(Server_Activity.this,"vova","12345678"));
//
////            wifiManager = new MyWiFIAPManager((WifiManager) getSystemService(Context.WIFI_SERVICE), "vova", "12345678");
////            try {
////                wifiManager.createWifiAccessPoint();
////            } catch (SocketException e) {
////                e.printStackTrace();
////            }
//            h = new Handler() {
//                public void handleMessage (final Message msg) {
//
//                    Server_Activity.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            String chatLine = msg.getData().getString("msg");
//                            textView1.append(chatLine);
//                            textView1.append("\n");
//                            textView1.setVisibility(View.VISIBLE);
//                        }
//                    });
//                }
//            };
//
//        }
//
//
//
//        if(!isRotate){
//            h.sendMessage(Util.getMessageFromString("My Server " + Util.getLocalIpAddressString(), "msg"));
//        }
//
//        Log.d(LOG_D, Util.getLocalIpAddressString());
//
//        IntentFilter intFilt = new IntentFilter();
//        intFilt.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
//        registerReceiver(br, intFilt);
//        if (!isRotate) {
//            try {
//                wifiManager = futureCreateAP.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//
//            wifiManager.start();
////            Intent i = new Intent(Server_Activity.this,ServerWorkService.class);
////            i.putExtra("wifiManager",wifiManager);
////            startService(new Intent(i));
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    wifiManager.start(h);
////                }
////            }).start();
//
//        }

//        br = new BroadcastReceiver() {
//            // действия при получении сообщений
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getIntExtra("wifi_state", 0) == 10) {
//                    Log.d(Server.LOG_D, "Try interruptAll");
//                    wifiManager.exit();
//                }
//                Log.d("Debug:info", "!!!!!!!!!" + intent.getAction());
//            }
//        };


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
        // unregisterReceiver(br);
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