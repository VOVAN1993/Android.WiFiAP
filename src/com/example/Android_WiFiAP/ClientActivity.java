package com.example.Android_WiFiAP;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by root on 10.01.14.
 */
public class ClientActivity extends Activity {
    public final static String LOG_CLIENT = "LOG_CLIENT";
    private EditText editText;
    private TextView textView;
    private Client client;
    private boolean isRotate;
    private Intent receive_to_client_queue;
    private BroadcastReceiver receiverForUpdateChat;
    public final static String PARAM_MESS = "mess";
    public static final String BROADCAST_CHAT = "com.example.Android_WiFiAP.ClientChat";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        isRotate = false;
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.chat);

        if (savedInstanceState != null) {
            isRotate = savedInstanceState.getBoolean("isRotate");
        }

        receive_to_client_queue = new Intent(Client.BROADCAST_CLIENT_FOR_QUEUE);

        receiverForUpdateChat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mess = intent.getStringExtra(PARAM_MESS);
                textView.append(mess + '\n');
            }
        };
        IntentFilter intFiltForBroadcast = new IntentFilter(BROADCAST_CHAT);
        registerReceiver(receiverForUpdateChat, intFiltForBroadcast);

        if (!isRotate) {
            Intent intentServiceClietnWork = new Intent(this, ClientWorkService.class);
            startService(intentServiceClietnWork);
        }


//        if (savedInstanceState != null) {
//            isRotate = savedInstanceState.getBoolean("isRotate");
//            client = (Client) getLastNonConfigurationInstance();
//        }
//
//        Handler handler_for_chat = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                String chatLine = msg.getData().getString("msg");
//                textView.append(chatLine);
//                textView.append("\n");
//            }
//        };
//        if (!isRotate) {
//            WifiManager wifiManager = (WifiManager)ClientActivity.this.getSystemService(Context.WIFI_SERVICE);
//            wifiManager.setWifiEnabled(true);
//           // startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
//            client = new Client(handler_for_chat);
//            client.startWork();
//        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return client;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRotate", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent intent = new Intent(ClientWorkService.BROADCAST_KILL_MYSELF);
//        sendBroadcast(intent);
    }

    public void clientonClickButton(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                Log.d(LOG_CLIENT, "Send button has been pressed");
                String text = editText.getText().toString();
                if (text.trim().length() == 0) {
                    Log.d(LOG_CLIENT, "Text is empty");
                } else {
                    receive_to_client_queue.putExtra(Client.PARAM_MESS_QUEUE, text);
                    sendBroadcast(receive_to_client_queue);
                    Log.d(LOG_CLIENT, "Sending message mess = " + text);
                }
                editText.setText("");
                break;
            case R.id.exit_button:
                Intent intent = new Intent(ClientWorkService.BROADCAST_KILL_MYSELF);
                sendBroadcast(intent);
                finish();
                break;
            default:
                Log.d(LOG_CLIENT, "Unknown button");
        }
    }

}