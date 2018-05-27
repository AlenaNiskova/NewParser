package com.alena;

import com.alena.Records.Record;
import com.alena.Threads.Pics;
import com.alena.Threads.Stores;
import com.alena.Threads.Texts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    static String ProjectsPath = "C:\\Users\\Alena\\IdeaProjects\\";

    public static void main(String[] args) {
        int port = 6666;
        try {
            ServerSocket ss = new ServerSocket(port);
            Socket socket = ss.accept();

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            String line = null;

            System.setProperty("webdriver.chrome.driver", ProjectsPath + "NewParser\\chromedriver.exe");

            ChromeOptions options = new ChromeOptions();
            options.addArguments("disable-infobars");
            options.addArguments("--start-maximized");

            WebDriver driver = new ChromeDriver(options);
            WebElement dynamicElement;
            //driver.get("https://www.podrygka.ru/catalog/podarki/?PAGEN_1=2&sortBy=property_rating&order=asc");
            //driver.get("https://www.podrygka.ru/catalog/makiyazh/");

            picsDirectory();

            boolean wasParsed = false;
            List<WebElement> ProductsUrl;
            List<WebElement> NextEl;
            //String Next = driver.getCurrentUrl();
            List<String> ThisTab = new ArrayList<String>();
            Thread texts, stores, pics;
            String Next = "https://www.podrygka.ru/catalog/podarki/";
            //String Next = "https://www.podrygka.ru/catalog/podarki/?PAGEN_1=2&sortBy=property_rating&order=asc";

            while (Next != null) {
                out.writeUTF("Locked");
                System.out.println("Файл открыт для записи.");

                driver.get(Next);

                FirstGUI firstGUI = new FirstGUI(driver);

                while (firstGUI.isVisible) {
                    Thread.sleep(1000);
                }

                JSONWork jsonWork = new JSONWork("JSON.json");

                dynamicElement = (new WebDriverWait(driver, 10))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body > div.page-wrapper > header > div.header-logo")));
                ProductsUrl = driver.findElements(By.cssSelector("div.products-list-item__header > a[href]"));
                Next = null;
                ThisTab.clear();
                for (WebElement el : ProductsUrl) {
                    ThisTab.add(el.getAttribute("href"));
                }
                NextEl = driver.findElements(By.cssSelector("div > a.pagination__button.pagination__button--right"));
                for (WebElement elUrl : NextEl) {
                    Next = elUrl.getAttribute("href");
                }
                for (String url : ThisTab) {
                    driver.get(url);
                    dynamicElement = (new WebDriverWait(driver, 10))
                            .until(ExpectedConditions.presenceOfElementLocated(By.className("product-detail__top")));
                    texts = new Thread(new Texts(driver, jsonWork), "TextsParsing");
                    pics = new Thread(new Pics(driver, jsonWork), "PicsParsing");
                    stores = new Thread(new Stores(driver, jsonWork), "StoresParcing");
                    texts.start();
                    stores.start();
                    pics.start();
                    try {
                        texts.join();
                        stores.join();
                        pics.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wasParsed = true;
                }

                RandomAccessFile RAF = new RandomAccessFile("JSON.json", "rw");
                long wheretopast;
                if (wasParsed) { wheretopast = RAF.length()-5; }
                else { wheretopast = RAF.length(); }
                RAF.seek(wheretopast);
                RAF.write("\n ]\n}".getBytes());
                RAF.close();
                out.writeUTF("Unlocked");
                System.out.println("Запись в файл завершена.");
            }

            JSONWork jsonWork = new JSONWork("JSON.json");
            do {
                line = in.readUTF();
            } while (line.startsWith("Locked"));
            System.out.println("Файл открыт для чтения данным процессом.");
            out.writeUTF("Locked");
            Set<Record> JSON = jsonWork.FromJSONSet();
            out.writeUTF("Unlocked");
            System.out.println("Чтение из файла завершено.");
            driver.quit();
            GUI gui = new GUI(JSON);
            out.writeUTF("Finished");
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void picsDirectory() {
        File fold = new File("Pictures");
        if (!fold.exists()) {
            fold.mkdirs();
        }
    }
}
