package com.example.Android_WiFiAP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by root on 10.01.14.
 */
public class Client {

    private ThreadGroup mThreadGroup;
    private InetAddress server_ip;
    private final int server_port;
    private Socket socket;
    private final BlockingQueue<String> queue;
    private final Handler handler_for_text_view;
    private Intent intent_for_chat;
    private BroadcastReceiver receiver_for_queue;
    public static final String PARAM_MESS_QUEUE = "MESS";
    public static final String BROADCAST_CLIENT_FOR_QUEUE = "com.example.Android_WiFi_AP.queue";
    private Context mContext;
    private ExecutorService mainPoll;

    public Client(Handler _handler_for_text_view, Context _context) {
        mThreadGroup = new ThreadGroup("my thread group");
        mainPoll = Executors.newFixedThreadPool(2);
        mContext = _context;
        handler_for_text_view = _handler_for_text_view;
        queue = new ArrayBlockingQueue<String>(1000);
        intent_for_chat = new Intent(ClientActivity.BROADCAST_CHAT);
        server_port = 6666;
        try {
            server_ip = InetAddress.getByName("192.168.43.1");
        } catch (UnknownHostException e) {
            Log.d(ClientActivity.LOG_CLIENT, "UnknownHostException");
        }

        receiver_for_queue = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mess = intent.getStringExtra(PARAM_MESS_QUEUE);
                queue.add(mess);
            }
        };
        IntentFilter intFiltForBroadcast = new IntentFilter(BROADCAST_CLIENT_FOR_QUEUE);
        mContext.registerReceiver(receiver_for_queue, intFiltForBroadcast);
    }

    public void startWork() {
        try {
            socket = new Socket(server_ip, server_port); // создаем сокет используя IP-адрес и порт сервера.
            //handler_for_text_view.sendMessage(Util.getMessageFromString("Da", "msg"));
            intent_for_chat.putExtra(ClientActivity.PARAM_MESS, "Connect OK");
            mContext.sendBroadcast(intent_for_chat);
            Log.d(ClientActivity.LOG_CLIENT, "OK:Connection succeeds");
            new Thread(mThreadGroup, new Sender(new DataOutputStream(socket.getOutputStream()))).start();
            new Thread(mThreadGroup, new Recv(new DataInputStream(socket.getInputStream()))).start();
//            mainPoll.submit(new Sender(new DataOutputStream(socket.getOutputStream())));
//            mainPoll.submit(new Recv(new DataInputStream(socket.getInputStream())));
        } catch (IOException e) {
            Log.d(ClientActivity.LOG_CLIENT, "I/O Error:" + e.getMessage());
        } finally {
            mainPoll.shutdown();
            Log.d(ClientActivity.LOG_CLIENT, "Pool shotdown");
        }

    }

    public void killSelf() {
        try {
            Log.d(ClientActivity.LOG_CLIENT, "Try kill self");
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            Log.d(ClientActivity.LOG_CLIENT, "ERROR I/O when try killing myself");
        }
    }

    public void addMess(String mess) {
        queue.add(mess);
    }


    private class Sender implements Runnable {
        private final DataOutputStream out;
        private final ExecutorService pool;

        private Sender(DataOutputStream out) {
            this.out = out;
            pool = Executors.newFixedThreadPool(5);
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    final String mess = queue.take();
                    Log.d(ClientActivity.LOG_CLIENT, "Trying sending message mess = " + mess);
                    pool.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                out.writeUTF(mess);
                                out.flush();
                                Log.d(ClientActivity.LOG_CLIENT, "OK send");
                            } catch (IOException e) {
                                Log.d(ClientActivity.LOG_CLIENT, "Error I/O when sending messege " + e);
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
                Log.d(ClientActivity.LOG_CLIENT, "Interrupt QUeueThread");
            } finally {
                try {
                    out.close();
                    pool.shutdown();
                    Log.d(ClientActivity.LOG_CLIENT, "Interrupt Sender");
                } catch (IOException e) {
                    Log.d(ClientActivity.LOG_CLIENT, "Error I/O when finally" + e);
                }
            }
        }
    }


    private class Recv implements Runnable {
        private final DataInputStream in;

        private Recv(DataInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {

                    String line = in.readUTF();
                    Log.d(ClientActivity.LOG_CLIENT, "Read " + line);
//                    handler_for_text_view.sendMessage(Util.getMessageFromString(line, "msg"));
                    intent_for_chat.putExtra(ClientActivity.PARAM_MESS, line);
                    mContext.sendBroadcast(intent_for_chat);

                }
            } catch (IOException e) {
                e.printStackTrace();
//                Thread.currentThread().interrupt();
                mThreadGroup.interrupt();
            } finally {
                Log.d(ClientActivity.LOG_CLIENT, "Interrupt Recv");
            }
        }
    }
}
