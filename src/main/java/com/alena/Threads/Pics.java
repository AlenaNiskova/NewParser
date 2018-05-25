package com.alena.Threads;

import com.alena.JSONWork;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class Pics implements Runnable {
    WebDriver driver;
    JSONWork jsonWork;

    public Pics(WebDriver driver, JSONWork jsonWork) {
        this.driver = driver;
        this.jsonWork = jsonWork;
    }

    public void run() {
        String id = "";
        String src, link;
        List<String> ImagePath = new ArrayList<String>();

        for (WebElement e: driver.findElements(By.className("product-detail__articul"))) {
            id = e.findElement(By.className("value")).getText();
        }
        for (WebElement e: driver.findElements(By.cssSelector("div.slick-list.draggable > div.slick-track > div > img"))) {
            src = e.getAttribute("src");
            link = save(src);
            ImagePath.add(link);
        }
        jsonWork.addPics(id, ImagePath);
    }

    private String save(String url) {
        String[] ar = url.split("/");
        try {
            ReadableByteChannel in = Channels.newChannel(
                    new URL(url).openStream());
            FileChannel out = new FileOutputStream(
                    "Pictures/"+ar[ar.length-1]).getChannel();
            out.transferFrom(in, 0, Long.MAX_VALUE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "Pictures/"+ar[ar.length-1];
    }
}
