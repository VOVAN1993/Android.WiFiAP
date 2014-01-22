package com.example.Android_WiFiAP.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.example.Android_WiFiAP.ClientScanResult;
import com.example.Android_WiFiAP.Server;
import com.example.Android_WiFiAP.Server_Activity;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by root on 02.01.14.
 */
public class Util {
    static Intent intent_for_chat = new Intent(Server_Activity.BROADCAST_SERVER_ACTIVITY);

    static public void customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        System.out.println(value + "  " + pattern + "  " + output);
    }

    public static String getLocalIpAddressString() {
        String ans = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress().toString();
                        // if (inetAddress.getHostAddress().toString().matches("..:..:..:.."))
                        ans += inetAddress.getHostAddress().toString();
                        ans += '\n';
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Debug:IPADDRESS", ex.getMessage());
        }
        return ans;
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


    public static String ping(String _ip) {

        try {
            String command = "ping -c 20 " + _ip.substring(1);
            Process p = null;
            p = Runtime.getRuntime().exec(command);
            int status = p.waitFor();
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            String bufferStr = buffer.toString();
            return bufferStr;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ping_over_socket(Socket socket, int count) {

        //незабудь в главном потоке чтения закоментить все!!!
        try {
            InputStream sin = socket.getInputStream();
            DataInputStream in = new DataInputStream(sin);
            OutputStream sout = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(sout);
            long total = 0L;
            Date date1 = new Date();
            for (int i = 0; i < count; i++) {
                out.writeInt(5);
                out.flush();
                int ret_int = in.readInt();
            }
            Date date2 = new Date();
            total = date2.getTime() - date1.getTime();
            Log.d(Server_Activity.LOG_D, "ping =" + total / count);
        } catch (IOException e) {
            Log.d(Server.LOG_D, "OK:Error I/O " + e);
        } finally {
            Log.d(Server.LOG_D, "Close client socket");
        }
    }

    public static void send(Socket socket, String mess) throws IOException {
        OutputStream sout = socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(sout);
        Log.d(Server_Activity.LOG_D, socket.getPort() + mess);
        out.writeUTF(mess);
        out.flush();
    }

    public static void send(DataOutputStream out, String mess, int port) throws IOException {
        Log.d(Server_Activity.LOG_D, port + mess);
        out.writeUTF(mess);
        out.flush();
    }

    public static void testSpeed(DataInputStream in, DataOutputStream out) {
        try {
            String line = null;
            int t = 0;
            long count = in.readLong();
            for (int i = 0; i < count; i++) {
                line = in.readUTF();
                t += line.length();
            }
            Log.d(Server.LOG_D, new Integer(t).toString() + " " + count);
            Date curDate = new Date();
            out.writeLong(12);
            Log.d(Server.LOG_D, "Date=" + curDate);
        } catch (IOException e) {
            Log.d(Server.LOG_D, "OK:Error I/O " + e);
        } finally {
            Log.d(Server.LOG_D, "Close client socket");
        }
    }


    public static void sendToTextViewServer(String mess, Context mContext) {
        intent_for_chat.putExtra(Server_Activity.SERVER_TYPE, Server_Activity.TYPE_SERVER_UPDATE_TEXTVIEW);
        intent_for_chat.putExtra(Server_Activity.PARAM_MESS, mess);
        mContext.sendBroadcast(intent_for_chat);
    }
}
