package com.example.Android_WiFiAP;

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

    private InetAddress server_ip;
    private int server_port;
    private Socket socket;
    private BlockingQueue<String> queue;
    private Handler handler_for_text_view;

    public Client(Handler _handler_for_text_view) {
        handler_for_text_view = _handler_for_text_view;
        queue = new ArrayBlockingQueue<String>(1000);
        server_port = 6666;
        try {
            server_ip = InetAddress.getByName("192.168.43.1");
        } catch (UnknownHostException e) {
            Log.d(ClientActivity.LOG_CLIENT, "UnknownHostException");
        }
    }

    public void startWork() {
        try {
            socket = new Socket(server_ip, server_port); // создаем сокет используя IP-адрес и порт сервера.
            handler_for_text_view.sendMessage(Util.getMessageFromString("Da", "msg"));
            Log.d(ClientActivity.LOG_CLIENT, "OK:Connection succeeds");
            new Thread(new Sender(new DataOutputStream(socket.getOutputStream()))).start();
            new Thread(new Recv(new DataInputStream(socket.getInputStream()))).start();
        } catch (IOException e) {
            Log.d(ClientActivity.LOG_CLIENT, "I/O Error:" + e.getMessage());
        }

    }

    public void addMess(String mess) {
        queue.add(mess);
    }


    private class Sender implements Runnable {
        private final DataOutputStream out;
        private ExecutorService pool;

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
                Thread.currentThread().interrupt();
                e.printStackTrace();
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
        private DataInputStream in;

        private Recv(DataInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {

                    String line = in.readUTF();
                    Log.d(ClientActivity.LOG_CLIENT, "Read " + line);
                    handler_for_text_view.sendMessage(Util.getMessageFromString(line, "msg"));

                }
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } finally {
                Log.d(ClientActivity.LOG_CLIENT, "Interrupt Recv");
            }
        }
    }
}
