package com.example.Android_WiFiAP;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.*;
import java.net.Socket;


public class ClientHandler implements Runnable {
    Socket incom;

    public ClientHandler(Socket _incom) {
        incom = _incom;
    }

    @Override
    public void run() {
        try {
            InputStream sin = incom.getInputStream();
            OutputStream sout = incom.getOutputStream();
            NsdServiceInfo s;

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            String line = null;
            //                while(true) {
            while (true) {
                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                Log.d("Debug:info", "The dumb client just sent me this line : " + line);
                Log.d("Debug:info", "I'm sending it back...");
                out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
                out.flush(); // заставляем поток закончить передачу данных.
                Log.d("Debug:info", "Waiting for the next line...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                incom.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
