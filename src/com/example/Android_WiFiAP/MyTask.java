package com.example.Android_WiFiAP;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;

class MyTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("Debug:info begin", "");
    }

    @Override
    protected Void doInBackground(Void... params) {
        int port = 8000;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Log.d("Debug:info", "Waiting for a client...");
            while (true) {
                Socket socket = serverSocket.accept();
                Log.d("Debug:info", "Got a client :) ... Finally, someone saw me through all the cover!");
                Runnable clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
                // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
//                InputStream sin = socket.getInputStream();
//                OutputStream sout = socket.getOutputStream();
//
//                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
//                DataInputStream in = new DataInputStream(sin);
//                DataOutputStream out = new DataOutputStream(sout);
//
//                String line = null;
////                while(true) {
//                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
//                Log.d("Debug:info", "The dumb client just sent me this line : " + line);
//                Log.d("Debug:info", "I'm sending it back...");
//                out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
//                out.flush(); // заставляем поток закончить передачу данных.
//                Log.d("Debug:info", "Waiting for the next line...");
//                socket.close();
            }
//                }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.d("Debug:info end", "");
    }
}