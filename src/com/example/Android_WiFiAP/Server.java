package com.example.Android_WiFiAP;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.example.Android_WiFiAP.Utils.Util;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    public static final String LOG_D = "Debug:Server";


    private class Client extends AbstractClient {
        protected Client(int _port, InetAddress _IPAddress, Socket _socket) {
            super(_port, _IPAddress, _socket);
        }
    }

    private final BlockingQueue<Pair<String, Client>> mMessageQueue;
    public List<Client> mClients;
    private Intent intent_for_chat;
    private ThreadGroup mThreadGroup;
    private final int mServerPort = 6666;
    private ServerSocket mServerSocket;
    private final int MAX_LENGTH_QUEUE = 1000;
    private Handler mTextViewHandler;
    private Thread mServerThread;
    private Thread mServerRecv;
    private Context mContext;

    public Server(Handler _handler, Context _context) {
        mContext = _context;
        intent_for_chat = new Intent(Server_Activity.BROADCAST_TEXT);
        mThreadGroup = new ThreadGroup("my thread group");
        mTextViewHandler = _handler;
        mClients = new CopyOnWriteArrayList();
//        mClients = Collections.synchronizedList(new LinkedList<Client>());
        mMessageQueue = new ArrayBlockingQueue<Pair<String, Client>>(MAX_LENGTH_QUEUE);
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
            Log.d(LOG_D, "Count client after remove" + mClients.size());
        } catch (IOException e) {
            Log.d(LOG_D, "Error I/O: close socket " + e);
        }
    }

    public synchronized void interruptAll() {
        Log.d(LOG_D, "Main ServerThread interrupt " + mThreadGroup.activeCount());
        try {
            if (mServerSocket != null)
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
            if (mServerSocket != null)

                if (mServerSocket != null) {
                    mServerSocket.close();
                    Log.d(LOG_D, "OK: Succesfull try to close the server socket");
                }
        } catch (IOException e) {
            Log.d(LOG_D, "Error I/O: Unsuccesfull try to close the server socket");
        }
    }

    private class ServerRecv implements Runnable {

        @Override
        public void run() {
            ExecutorService pool = Executors.newFixedThreadPool(5);
            while (!Thread.currentThread().isInterrupted() || !mMessageQueue.isEmpty()) {
                try {
                    Pair<String, Client> pair = mMessageQueue.take();
                    Log.d(LOG_D, "Send message: " + pair.first + " from " + pair.second);
                    Future f = pool.submit(new ServerSend(pair.second, mClients, pair.first));
                    intent_for_chat.putExtra(Server_Activity.PARAM_MESS, pair.first);
                    mContext.sendBroadcast(intent_for_chat);
//                    mTextViewHandler.sendMessage(Util.getMessageFromString(pair.first, "msg"));
                } catch (InterruptedException e) {
                    Log.d(LOG_D, "Ne doljno bit' (obrabotano v while,hota hz) " + e);
                    mThreadGroup.interrupt();
                }
            }
            pool.shutdown();
            Log.d(LOG_D, "OK:Interrupt ServerRecv. Pool is shutdown");
        }

        private class ServerSend implements Runnable {
            private Client clientFrom;
            private List<Client> listClients;
            private String mess;

            private ServerSend(Client clientFrom, List<Client> _listClients, String mess) {
                this.clientFrom = clientFrom;
                this.listClients = _listClients;
                this.mess = mess;
            }

            @Override
            public void run() {
                for (Client client : listClients) {
                    if (!(client.getPort() == clientFrom.getPort() && client.getIPAddress().equals(clientFrom.getIPAddress()))) {
                        try {
                            Util.send(client.getSocket(), mess);
                            Log.d(LOG_D, "Send to " + client.getIPAddress().toString() + client.getPort());
                        } catch (IOException e) {
                            Log.d(LOG_D, "Not send.Client is removed ip=" + client.getIPAddress().toString());
                        }
                    }
                }
            }
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
//                    mTextViewHandler.sendMessage(Util.getMessageFromString("client " + socket_new_client.getInetAddress().toString(), "msg"));
                    intent_for_chat.putExtra(Server_Activity.PARAM_MESS, "client " + socket_new_client.getInetAddress().toString());
                    mContext.sendBroadcast(intent_for_chat);
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

                    DataOutputStream out = new DataOutputStream(sout);
                    String line = null;
                    while (!Thread.currentThread().isInterrupted()) {
                        line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.

                        mMessageQueue.add(new Pair<String, Client>(line, mClient));
                        Log.d(LOG_D, "The dumb client just sent me this line : " + line);

                    }
                    Log.d(LOG_D, "ServerHandler interrupted");
                } catch (IOException e) {
                    Log.d(LOG_D, "OK:Error I/O " + e);
                } finally {
                    Util.sendToTextViewServer(mClient.getIPAddress() + " was removed!", mContext);
                    Log.d(LOG_D, "Close client socket");
                    Log.d(LOG_D, "count clients = " + mClients.size());
                    removeClient(mClient);//TODO: возможно не надо. посмотреть козда i/o exception кидает readUTF
                }

            }
        }
    }


    public int getServerPort() {
        return mServerPort;
    }

    public int getCountClients() {
        return mClients.size();
    }

    public AbstractClient getClient() {
        assert mClients.size() > 0;
        return mClients.get(0);
    }

}