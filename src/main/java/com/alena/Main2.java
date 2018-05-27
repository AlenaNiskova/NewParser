package com.alena;

import com.alena.Records.Record;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Main2 {

    public static String jsonpath = "JSON.json";

    public static void main(String[] args) {
        int serverPort = 6666;
        int inDB=0;
        String address = "192.168.1.43";
        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            Socket socket = new Socket(ipAddress, serverPort);

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            Map<String, Record> JSON = new HashMap<>();

            DataBase db = new DataBase();/*
                inDB = db.count + 1;*/

            try {
                String line;
                do {
                    do {
                        line = in.readUTF();
                    }
                    while (line.startsWith("Locked"));
                    System.out.println("Файл открыт для чтения данным процессом.");
                    out.writeUTF("Locked");
                    Set<Record> Set = JSONList(jsonpath/*, inDB*/);
                    for (Record x : Set) {
                        JSON.put(x.getId(), x);
                    }
                    out.writeUTF("Unlocked");
                    System.out.println("Чтение из файла завершено.");
                    db.add(JSON);
                    do {
                        line = in.readUTF();
                    }
                    while (line.startsWith("Locked"));
                    line = in.readUTF();
                }
                while (!line.startsWith("Finished"));
            } finally {
                db.close();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static Set<Record> JSONList(String path/*, int n*/) {
        Set<Record> From = new LinkedHashSet<>();
        String st, strec;
        Record rec;
        int count = 0;
        try {
            RandomAccessFile RAF = new RandomAccessFile(path, "rw");
            RAF.seek(12);
            st = "";
            Gson gson = new Gson();
            /*while ((st!=null) && ((!st.startsWith("{")) || (count!=n)) && (!st.startsWith(" ]"))) {
                st = RAF.readLine();
                if (st!=null) {
                    if (st.startsWith("{")) {
                        count++;
                    }
                }
            }*/
            while ((st!=null) && (!st.startsWith(" ]"))) {
                strec = "";
                while (!st.startsWith("}")) {
                    strec = strec + st;
                    st = RAF.readLine();
                }
                strec = strec + st;
                st = RAF.readLine();
                if (st==null) {return null;}
                if (!st.startsWith(" ]")) {
                    strec = strec.substring(0, strec.length()-1);
                }
                strec = new String(strec.getBytes("ISO-8859-1"), "UTF-8");
                rec = gson.fromJson(strec, Record.class);
                From.add(rec);
            }
            RAF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return From;
    }
}
