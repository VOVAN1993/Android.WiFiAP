package com.example.Android_WiFiAP;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);

        isRotate = false;
        if (savedInstanceState != null) {
            isRotate = savedInstanceState.getBoolean("isRotate");
            client = (Client) getLastNonConfigurationInstance();
        }
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.chat);
        Handler handler_for_chat = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                textView.append(chatLine);
                textView.append("\n");
            }
        };
        if (!isRotate) {
            client = new Client(handler_for_chat);
            client.startWork();
        }
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

    public void onClickButton(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                Log.d(LOG_CLIENT, "Send button has been pressed");
                String text = editText.getText().toString();
                if (text.trim().length() == 0) {
                    Log.d(LOG_CLIENT, "Text is empty");
                } else {
                    client.addMess(text);
                    //Log.d(LOG_CLIENT,"Sending message mess = "+text);
                }
                editText.setText("");
                break;
            default:
                Log.d(LOG_CLIENT, "Unknown button");
        }
    }

}