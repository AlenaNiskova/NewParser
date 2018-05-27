package com.alena;

import com.alena.Records.Record;
import com.alena.Records.Review;
import com.alena.Records.Store;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JSONWork {

    private ConcurrentMap<String, Record> toJSONMap = new ConcurrentHashMap<String, Record>();
    private Set<String> IDinJson = new HashSet<String>();
    private final String jsonpath;
    private boolean isFileExists;
    private long pos;

    public JSONWork(String jsonpath) {
        this.jsonpath = jsonpath;
        File file = new File(jsonpath);
        isFileExists = file.exists();
        try {
            RandomAccessFile RAFile = new RandomAccessFile(jsonpath, "rw");
            if (!isFileExists) {
                RAFile.writeBytes("{ \"array\": [\n");
                pos = RAFile.getFilePointer();
            }
            else {
                String id = "";
                while (!id.startsWith(" ]")) {
                    id = RAFile.readLine();
                    if (id.contains("\"id\":")) {
                        IDinJson.add(id.substring(9, id.length()-2));
                    }
                }
                pos = RAFile.getFilePointer()-4;
            }
            RAFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addText(String id, String text) {
        Record rec;
        if (toJSONMap.containsKey(id)) { rec = toJSONMap.get(id);}
        else { rec = new Record(id);}
        text = text.replace("'", "\\'");
        rec.setText(text);
        check(rec);
    }

    public synchronized void addPics(String id, List<String> pics) {
        Record rec;
        if (toJSONMap.containsKey(id)) { rec = toJSONMap.get(id);}
        else { rec = new Record(id);}
        rec.setPics(pics);
        check(rec);
    }

    public synchronized void addReview(String id, List<Review> reviews) {
        Record rec;
        if (toJSONMap.containsKey(id)) { rec = toJSONMap.get(id);}
        else { rec = new Record(id);}
        rec.setReviews(reviews);
        check(rec);
    }

    public synchronized void addStores(String id, List<Store> stores) {
        Record rec;
        if (toJSONMap.containsKey(id)) { rec = toJSONMap.get(id);}
        else { rec = new Record(id);}
        rec.setStores(stores);
        check(rec);
    }

    private void check(Record rec) {
        if ((rec.getText()!=null)&&(rec.getReviews()!=null)&&(rec.getStores()!=null)&&(rec.getPics()!=null)) {
            if (!IDinJson.contains(rec.getId())) {
                write(rec);
                IDinJson.add(rec.getId());
            }
            toJSONMap.remove(rec.getId());
        }
        else { toJSONMap.put(rec.getId(), rec);}
    }

    private void write(Record rec) {
        try {
            RandomAccessFile RAF = new RandomAccessFile(jsonpath, "rw");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String StToGSON;
            if (!isFileExists) {
                StToGSON = gson.toJson(rec) + "     ";
                isFileExists = true;
            } else {
                StToGSON = ",\n" + gson.toJson(rec) + "     ";
            }
            byte[] inputBytes = StToGSON.getBytes();
            RAF.seek(pos);
            RAF.write(inputBytes);
            pos = RAF.getFilePointer()-5;
            RAF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Record> FromJSONSet() {
        Set<Record> From = new LinkedHashSet<Record>();
        String st, strec;
        Record rec;
        try {
            RandomAccessFile RAF = new RandomAccessFile(jsonpath, "rw");
            RAF.seek(12);
            st = "";
            Gson gson = new Gson();
            while ((st!=null) && (!st.startsWith(" ]"))) {
                strec = "";
                while (!st.startsWith("}")) {
                    strec = strec + st;
                    st = RAF.readLine();
                }
                strec = strec + st;
                st = RAF.readLine();
                if (st!=null) {
                    if (!st.startsWith(" ]")) {
                        strec = strec.substring(0, strec.length() - 1);
                    }
                    strec = new String(strec.getBytes("ISO-8859-1"), "UTF-8");
                    rec = gson.fromJson(strec, Record.class);
                    From.add(rec);
                }
            }
            RAF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return From;
    }
}
