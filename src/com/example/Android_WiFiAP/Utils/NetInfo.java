package com.example.Android_WiFiAP.Utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by root on 17.01.14.
 */
public class NetInfo {
    public static final String NOIP = "0.0.0.0";
    public static final String NOMASK = "255.255.255.255";
    public static final String NOMAC = "00:00:00:00:00:00";
    private WifiInfo info;
    private int speed = 0;
    private String SSID;
    private String BSSID;
    private String macAddress = NOMAC;
    private String gatewayIp = NOIP;
    private String netmaskIp = NOMASK;

    public boolean getWifiInfo(Context ctxt) {
        WifiManager wifi = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            info = wifi.getConnectionInfo();
            // Set wifi variables
            speed = info.getLinkSpeed();
            SSID = info.getSSID();
            BSSID = info.getBSSID();
            macAddress = info.getMacAddress();
            gatewayIp = getIpFromIntSigned(wifi.getDhcpInfo().gateway);
            // broadcastIp = getIpFromIntSigned((dhcp.ipAddress & dhcp.netmask)
            // | ~dhcp.netmask);
            netmaskIp = getIpFromIntSigned(wifi.getDhcpInfo().netmask);
            return true;
        }
        return false;
    }

    public static long getUnsignedLongFromIp(String ip_addr) {
        String[] a = ip_addr.split("\\.");
        return (Integer.parseInt(a[0]) * 16777216 + Integer.parseInt(a[1]) * 65536
                + Integer.parseInt(a[2]) * 256 + Integer.parseInt(a[3]));
    }

    public static String getIpFromIntSigned(int ip_int) {
        String ip = "";
        for (int k = 0; k < 4; k++) {
            ip = ip + ((ip_int >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    public static String getIpFromLongUnsigned(long ip_long) {
        String ip = "";
        for (int k = 3; k > -1; k--) {
            ip = ip + ((ip_long >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    public String getBSSID() {
        return BSSID;
    }

    public WifiInfo getInfo() {
        return info;
    }

    public int getSpeed() {
        return speed;
    }

    public String getSSID() {
        return SSID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public String getNetmaskIp() {
        return netmaskIp;
    }


}
