package com.example.Android_WiFiAP;


import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server {
    public static final String LOG_D = "Debug:Server";

    private class Client extends AbstractClient {
        protected Client(int _port, InetAddress _IPAddress, Socket _socket) {
            super(_port, _IPAddress, _socket);
        }
    }

    private BlockingQueue<String> mMessageQueue;
    public List<Client> mClients;

    private final int mServerPort = 6666;
    private ServerSocket mServerSocket;
    private final int MAX_LENGTH_QUEUE = 1000;
    private Handler mTextViewHandler;

    public Server(Handler _handler) {
        mTextViewHandler = _handler;
        mClients = Collections.synchronizedList(new LinkedList<Client>());
        mMessageQueue = new ArrayBlockingQueue<String>(MAX_LENGTH_QUEUE);
        new Thread(new ServerThread()).start();
        new Thread(new ServerRecv()).start();
    }

    /*
    @hide
     */
    private void closeSocket(Socket _sock) {
        try {
            _sock.close();
        } catch (IOException e) {
            Log.d(LOG_D, "Error I/O:close Socket");
        }
    }

    private class ServerRecv implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() || !mMessageQueue.isEmpty()) {
                try {
                    mTextViewHandler.sendMessage(Util.getMessageFromString(mMessageQueue.take(), "msg"));
                } catch (InterruptedException e) {
                    Log.d(LOG_D, "Ne doljno bit' (obrabotano v while,hota hz) " + e);
                }
            }
        }
    }

    private class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(getServerPort());
                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(LOG_D, "Waiting for a client...");
                    Socket socket_new_client = mServerSocket.accept();
                    Client newClient = new Client(socket_new_client.getPort(), socket_new_client.getInetAddress(), socket_new_client);
                    mClients.add(newClient);
                    Log.d(LOG_D, "New client: " + newClient.getIPAddress() + " port=" + newClient.getPort());
                    Log.d(LOG_D, "mClients.size()=" + mClients.size());
                    Runnable run = new ServerHandler(socket_new_client);
                    Thread t = new Thread(run);
                    t.start();
                }
            } catch (IOException e) {
                Log.d(LOG_D, "IOException if an error occurs while creating the socket.\n" + e);
            }
        }


        private class ServerHandler implements Runnable {
            private Socket mClientSocket;

            private ServerHandler(Socket mClientSocket) {
                this.mClientSocket = mClientSocket;
            }

            @Override
            public void run() {
                InputStream sin = null;
                try {
                    sin = mClientSocket.getInputStream();
                    DataInputStream in = new DataInputStream(sin);
                    String line = null;
                    while (!Thread.currentThread().isInterrupted()) {
                        line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                        mMessageQueue.add(line);
                        Log.d(LOG_D, "The dumb client just sent me this line : " + line);
                    }
                } catch (IOException e) {
                    Log.d(LOG_D, "Error I/O " + e);
                } finally {
                    closeSocket(mClientSocket);
                }

            }
        }
    }


    public int getServerPort() {
        return mServerPort;
    }
}