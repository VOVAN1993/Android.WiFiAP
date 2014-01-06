package com.example.Android_WiFiAP;


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

    public Server() {
        mClients = Collections.synchronizedList(new LinkedList<Client>());
        mMessageQueue = new ArrayBlockingQueue<String>(MAX_LENGTH_QUEUE);
        new Thread(new ServerThread()).run();
    }


    private class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(getServerPort());
                while (true) {
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
                while (true) {
                    line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                    Log.d(LOG_D, "The dumb client just sent me this line : " + line);
                }
            } catch (IOException e) {
                Log.d(LOG_D, "Error I/O " + e);
            }

        }
    }

//    public static void main(String[] ar) {
//        int port = 6666; // случайный порт (может быть любое число от 1025 до 65535)
//        try {
//
//            ServerSocket ss = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
//            System.out.println("Waiting for a client...");
//
//            Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
//            System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
//            System.out.println();
//
//            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
//            InputStream sin = socket.getInputStream();
//            OutputStream sout = socket.getOutputStream();
//
//            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
//            DataInputStream in = new DataInputStream(sin);
//            DataOutputStream out = new DataOutputStream(sout);
//
//            String line = null;
//            while (true) {
//                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
//                System.out.println("The dumb client just sent me this line : " + line);
//                System.out.println("I'm sending it back...");
//                out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
//                out.flush(); // заставляем поток закончить передачу данных.
//                System.out.println("Waiting for the next line...");
//                System.out.println();
//            }
//        } catch (Exception x) {
//            x.printStackTrace();
//        }
//    }

    public int getServerPort() {
        return mServerPort;
    }
}