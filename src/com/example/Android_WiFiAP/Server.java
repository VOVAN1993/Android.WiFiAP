package com.example.Android_WiFiAP;


import android.os.Handler;
import android.util.Log;

import java.io.*;
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

    private ThreadGroup mThreadGroup;
    private final int mServerPort = 6666;
    private ServerSocket mServerSocket;
    private final int MAX_LENGTH_QUEUE = 1000;
    private Handler mTextViewHandler;
    private Thread mServerThread;
    private Thread mServerRecv;

    public Server(Handler _handler) {
        mThreadGroup = new ThreadGroup("my thread group");
        mTextViewHandler = _handler;
        mClients = Collections.synchronizedList(new LinkedList<Client>());
        mMessageQueue = new ArrayBlockingQueue<String>(MAX_LENGTH_QUEUE);
        mServerThread = new Thread(mThreadGroup, new ServerThread());
        mServerThread.start();
        mServerRecv = new Thread(mThreadGroup, new ServerRecv());
        mServerRecv.start();
    }

    private synchronized void removeClient(Client _client) {
        try {
            _client.getSocket().close();
            if (!mClients.remove(_client)) {
                Log.d(LOG_D, "Error: Unsuccessful try to remove the client");
            } else {
                Log.d(LOG_D, "OK: Successful try to remove the client");
            }
        } catch (IOException e) {
            Log.d(LOG_D, "Error I/O: close socket " + e);
        }
    }

    public synchronized void interruptAll() {
        Log.d(LOG_D, "Main ServerThread interrupt " + mThreadGroup.activeCount());
        try {
            mServerSocket.close();
        } catch (IOException e) {
            Log.d(LOG_D, "Hz cto");
        }
        mThreadGroup.interrupt();
    }

    private synchronized void closeSockets() {
        for (Client client : mClients) {
            removeClient(client);
        }
        try {
            mServerSocket.close();
            Log.d(LOG_D, "OK: Succesfull try to close the server socket");
        } catch (IOException e) {
            Log.d(LOG_D, "Error I/O: Unsuccesfull try to close the server socket");
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
                    mThreadGroup.interrupt();
                }
            }
            Log.d(LOG_D, "OK:Interrupt ServerRecv");
        }

    }

    private class ServerThread implements Runnable {
        private ThreadGroup mThreadGroup_local;

        public ServerThread() {
            mThreadGroup_local = new ThreadGroup(mThreadGroup, "my thread group LOCAL");
        }

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
                    ServerHandler mServerHandler = new ServerHandler(newClient);
                    Thread mServerHandlerThread = new Thread(mThreadGroup_local, mServerHandler);
                    mServerHandlerThread.start();
                }
            } catch (IOException e) {
                Log.d(LOG_D, "IOException if an error occurs while creating the socket.\n" + e);
            } finally {
                Log.d(LOG_D, "ServerThread block finally");
                closeSockets();
            }
            Log.d(LOG_D, "Interrupt all Thread ServerThread2222");
            mThreadGroup_local.interrupt();

        }


        private class ServerHandler implements Runnable {
            private Client mClient;

            private ServerHandler(Client mClient) {
                this.mClient = mClient;
            }

            @Override
            public void run() {
                try {
                    InputStream sin = mClient.getSocket().getInputStream();
                    DataInputStream in = new DataInputStream(sin);
                    OutputStream sout = mClient.getSocket().getOutputStream();

                    // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                    DataOutputStream out = new DataOutputStream(sout);
                    String line = null;
                    int t = 0;
                    while (!Thread.currentThread().isInterrupted()) {
                        line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                        mMessageQueue.add(line);
                        Log.d(LOG_D, "The dumb client just sent me this line : " + line);
                    }
                    Log.d(LOG_D, "ServerHandler interrupted");
                } catch (IOException e) {
                    Log.d(LOG_D, "OK:Error I/O " + e);
                } finally {
                    Log.d(LOG_D, "Close client socket");
                    removeClient(mClient);//TODO: возможно не надо. посмотреть козда i/o exception кидает readUTF
                }

            }
        }
    }


    public int getServerPort() {
        return mServerPort;
    }

    public AbstractClient getClient() {
        assert mClients.size() > 0;
        return mClients.get(0);
    }
}