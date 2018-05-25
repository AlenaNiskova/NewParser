package com.alena.Threads;

import com.alena.JSONWork;
import com.alena.Records.Store;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class Stores implements Runnable {
    WebDriver driver;
    JSONWork jsonWork;

    public Stores(WebDriver driver, JSONWork jsonWork) {
        this.driver = driver;
        this.jsonWork = jsonWork;
    }

    public void run() {
        String a;
        Store store;
        String id = "";
        List<WebElement> ava;
        List<Store> stores = new ArrayList<Store>();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (WebElement e: driver.findElements(By.className("product-detail__articul"))) {
            id = e.findElement(By.className("value")).getText();
        }
        for (WebElement e: driver.findElements(By.className("shops-list-item"))) {
            store = new Store();
            a = "";
            for (WebElement name: e.findElements(By.cssSelector("div.shops-list-item__name.shops-list-item__col > a > span"))) {
                a = (String) js.executeScript("var element = arguments[0];return element.textContent;", name) + "\n";
            }
            store.setName(a);
            ava = e.findElements(By.cssSelector("div.shops-list-item__availability.shops-list-item__col > div > div.availability__item.active"));
            store.setAvailability(ava.size());
            stores.add(store);
        }
        jsonWork.addStores(id, stores);
    }
}
