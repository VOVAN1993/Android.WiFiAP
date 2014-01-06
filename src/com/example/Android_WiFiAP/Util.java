package com.example.Android_WiFiAP;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by root on 02.01.14.
 */
public class Util {
    public static String getLocalIpAddressString() throws SocketException {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress().toString();
                        if (inetAddress.getHostAddress().toString().matches("..:..:..:.."))
                            Log.d("Debug:info!!!:", inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Debug:IPADDRESS", ex.getMessage());
        }
        return null;
    }

    public static ArrayList<ClientScanResult> getClientList() {
        BufferedReader br = null;
        ArrayList<ClientScanResult> result = null;

        try {
            result = new ArrayList<ClientScanResult>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                Log.d("Debug:line=", line);
                if ((splitted != null) && (splitted.length >= 4)) {
                    // Basic sanity check
                    String mac = splitted[3];

                    if (mac.matches("..:..:..:..:..:..")) {
                        result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static Message getMessageFromString(String str, String key) {
        Bundle messageBundle = new Bundle();
        messageBundle.putString(key, str);

        Message message = new Message();
        message.setData(messageBundle);
        return message;
    }
}
