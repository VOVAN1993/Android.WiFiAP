package com.example.Android_WiFiAP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by root on 04.01.14.
 */
public class MainActivity extends Activity {
    public static final String LOG_D = "Debug:main";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.client_button:
                Log.d(LOG_D, "Press button_client");
                break;
            case R.id.server_button:
                Log.d(LOG_D, "Press button_server");
                Intent intent = new Intent(this, Server_Activity.class);
                startActivity(intent);
                break;
        }
    }
}