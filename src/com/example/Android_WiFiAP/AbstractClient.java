package com.example.Android_WiFiAP;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by root on 06.01.14.
 */
public abstract class AbstractClient {
    private int mPort;
    private InetAddress mIPAddress;
    private Socket mSocket;

    public void setSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    AbstractClient(int mPort, InetAddress mIPAddress, Socket mSocket) {
        this.mPort = mPort;
        this.mIPAddress = mIPAddress;
        this.mSocket = mSocket;
    }

    public Socket getSocket() {

        return mSocket;
    }

    protected final void setPort(int _port) {
        this.mPort = _port;
    }

    protected final void setIPAddress(InetAddress _IPAddress) {
        this.mIPAddress = _IPAddress;
    }

    protected final int getPort() {
        return mPort;
    }

    protected final InetAddress getIPAddress() {
        return mIPAddress;
    }
}
