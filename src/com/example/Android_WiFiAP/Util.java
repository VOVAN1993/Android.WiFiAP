package com.example.Android_WiFiAP;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

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
    static public void customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        System.out.println(value + "  " + pattern + "  " + output);
    }

    public final static String getLocalIpAddressString() {
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

    public final static ArrayList<ClientScanResult> getClientList() {
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

    public final static Message getMessageFromString(String str, String key) {
        Bundle messageBundle = new Bundle();
        messageBundle.putString(key, str);

        Message message = new Message();
        message.setData(messageBundle);
        return message;
    }


    public final static String ping(String _ip) {

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

    public final static void ping_over_socket(Socket socket, int count) {

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

    public final static void send(Socket socket, String mess) throws IOException {
        OutputStream sout = socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(sout);
        Log.d(Server_Activity.LOG_D, socket.getPort() + mess);
        out.writeUTF(mess);
        out.flush();
    }

    public final static void send(DataOutputStream out, String mess, int port) throws IOException {
        Log.d(Server_Activity.LOG_D, port + mess);
        out.writeUTF(mess);
        out.flush();
    }

    public final static void testSpeed(DataInputStream in, DataOutputStream out) {
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
}
