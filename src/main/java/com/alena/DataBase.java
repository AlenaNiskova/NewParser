package com.alena;

import com.alena.Records.Record;
import com.alena.Records.Review;
import com.alena.Records.Store;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class DataBase {
    private final String url = "jdbc:mysql://127.0.0.1:3306/pod?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String pass = "MercifulSumkin!";
    private static Connection con;
    private static Statement state;
    private static ResultSet res;
    public int count;

    public DataBase() throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
        state = con.createStatement();
        count = CreateDB();
    }

    private int CreateDB() throws SQLException {
        String records = "CREATE TABLE IF NOT EXISTS `records` ( \n" +
                "  `Key` int NOT NULL AUTO_INCREMENT, \n" +
                "  `Id` varchar(10) UNIQUE, \n" +
                "  `Text` text, \n" +
                "  PRIMARY KEY (`Key`));";
        String pics = "CREATE TABLE IF NOT EXISTS `pics` (\n" +
                "  `Id` varchar(10),\n" +
                "  FOREIGN KEY(Id) REFERENCES `pod`.`records`(Id),\n" +
                "  `Pic` tinytext);";
        String reviews = "CREATE TABLE IF NOT EXISTS `reviews` (\n" +
                "  `Id` varchar(10),\n" +
                "  FOREIGN KEY(Id) REFERENCES `pod`.`records`(Id),\n" +
                "  `Rating` int, \n" +
                "  `Text` text);";
        String stores = "CREATE TABLE IF NOT EXISTS `stores` (\n" +
                "  `Id` varchar(10),\n" +
                "  FOREIGN KEY(Id) REFERENCES `pod`.`records`(Id),\n" +
                "  `Name` text, \n" +
                "  `Availability` int);";
        state.execute(records);
        state.execute(pics);
        state.execute(reviews);
        state.execute(stores);
        res = state.executeQuery("SELECT `Key` FROM records");
        res.getFetchSize();
        res.last();
        return res.getRow();
    }

    public void close() throws SQLException {
        res.close();
        state.close();
        con.close();
    }

    public Map<String, Record> check(Map<String, Record> Map) throws SQLException {
        String temp = "CREATE TEMPORARY TABLE IF NOT EXISTS `temp` (\n" +
                "  `Id` varchar(10));";
        state.execute(temp);
        con.setAutoCommit(false);
        for (Record x: Map.values()) {
            state.addBatch("INSERT INTO temp (Id) \n" +
                    "VALUES ('" + x.getId() + "');");
        }
        state.executeBatch();
        con.commit();
        res = state.executeQuery("SELECT Id FROM temp INNER JOIN records USING(`Id`)");
        while (res.next()) {
            Map.remove(res.getString(1));
        }
        return Map;
    }

    public void add(Map<String, Record> Map) throws SQLException {
        Map = check(Map);
        con.setAutoCommit(false);
        for (Record x: Map.values()) {
            state.addBatch("INSERT INTO records (Id, Text) \n" +
                    "VALUES ('" + x.getId() + "','" + x.getText() + "');");
        }
        state.executeBatch();
        for (Record x: Map.values()) {
            for (String pic : x.getPics()) {
                state.addBatch("INSERT INTO pics (Id, Pic) \n" +
                        "VALUES ('" + x.getId() + "','" + pic + "');");
            }
        }
        state.executeBatch();
        for (Record x: Map.values()) {
            for (Review review : x.getReviews()) {
                state.addBatch("INSERT INTO reviews (Id, Rating, Text) \n" +
                        "VALUES ('" + x.getId() + "'," + review.getRating() + ",'" + review.getText() + "');");
            }
        }
        state.executeBatch();
        for (Record x: Map.values()) {
            for (Store store : x.getStores()) {
                state.addBatch("INSERT INTO stores (Id, Name, Availability) \n" +
                        "VALUES ('" + x.getId() + "','" + store.getName() + "'," + store.getAvailability() + ");");
            }
        }
        state.executeBatch();
        con.commit();
    }

    public void Testadd(List<Record> List) throws SQLException {
        con.setAutoCommit(false);
        int k = 40;
        int m = 0;
        int n = List.size();
        Record x;
        while (n > 0) {
            for (int i = m * k; (i<m*k+k)&&(i<List.size()); i++) {
                x = List.get(i);
                state.addBatch("INSERT INTO records (Id, Text) \n" +
                        "VALUES (' " + x.getId() + " ',' " + x.getText() + " ');");
            }
            state.executeBatch();
            for (int i = m * k; (i<m*k+k)&&(i<List.size()); i++) {
                x = List.get(i);
                for (String pic: x.getPics()) {
                    state.addBatch("INSERT INTO pics (Id, Pic) \n" +
                            "VALUES (' " + x.getId() + " ',' " + pic + " ');");
                }
            }
            state.executeBatch();
            for (int i = m * k; (i<m*k+k)&&(i<List.size()); i++) {
                x = List.get(i);
                for (Review review: x.getReviews()) {
                    state.addBatch("INSERT INTO reviews (Id, Rating, Text) \n" +
                            "VALUES (' " + x.getId() + " '," + review.getRating() + ",' " + review.getText() + " ');");
                }
            }
            state.executeBatch();
            for (int i = m * k; (i<m*k+k)&&(i<List.size()); i++) {
                x = List.get(i);
                for (Store store: x.getStores()) {
                    state.addBatch("INSERT INTO stores (Id, Name, Availability) \n" +
                            "VALUES (' " + x.getId() + " ',' " + store.getName() + " '," + store.getAvailability() + ");");
                }
            }
            state.executeBatch();
            m++;
            n = n-k;
        }
        con.commit();
    }
}
