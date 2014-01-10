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
        Log.d(Server_Activity.LOG_D_FOR_ACT, "MainActivity.onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Server_Activity.LOG_D_FOR_ACT, "MainActivity.onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Server_Activity.LOG_D_FOR_ACT, "MainActivity.onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Server_Activity.LOG_D_FOR_ACT, "MainActivity.onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Server_Activity.LOG_D_FOR_ACT, "MainActivity.onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClickButton(View view) {
        Intent next = null;
        switch (view.getId()) {
            case R.id.client_button:
                Log.d(LOG_D, "Press button_client");
                next = new Intent(this, ClientActivity.class);
                startActivity(next);
                break;
            case R.id.server_button:
                Log.d(LOG_D, "Press button_server");
                next = new Intent(this, Server_Activity.class);
                startActivity(next);
                break;
        }
    }
}