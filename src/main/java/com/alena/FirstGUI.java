package com.alena;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class FirstGUI extends JFrame {
    public boolean isVisible = true;

    public FirstGUI(WebDriver driver) {
        JFrame frame = new JFrame("Очень сложно, до свидания.");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                isVisible = false;
                e.getWindow().setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        Update(frame, driver);
    }

    public void Update(JFrame frame, WebDriver driver) {
        WebElement Brand, Land, Line, Search, Stoke, Filter, arrow;
        String BrandSt, LandSt, LineSt, StokeSt, FilterSt;

        Map<String, WebElement> mCSB_11 = new LinkedHashMap<>();
        Map<String, WebElement> mCSB_12 = new LinkedHashMap<>();
        Map<String, WebElement> mCSB_13 = new LinkedHashMap<>();
        Map<String, WebElement> mCSB_14 = new LinkedHashMap<>();
        Map<String, WebElement> Filters = new LinkedHashMap<>();

        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body > div.page-wrapper > header > div.header-logo")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = "var object = arguments[0];"
                + "var theEvent = document.createEvent(\"MouseEvent\");"
                + "theEvent.initMouseEvent(\"click\", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "object.dispatchEvent(theEvent);";

        for (WebElement el: driver.findElements(By.cssSelector("#mCSB_11_container > div[class^='filter-list-item']"))) {
            Brand = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            BrandSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Brand);
            mCSB_11.put(BrandSt, el);
        }

        for (WebElement el: driver.findElements(By.cssSelector("#mCSB_12_container > div[class^='filter-list-item']"))) {
            Line = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            LineSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Line);
            mCSB_13.put(LineSt, el);
        }

        for (WebElement el: driver.findElements(By.cssSelector("#mCSB_13_container > div[class^='filter-list-item']"))) {
            Land = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            LandSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Land);
            mCSB_12.put(LandSt, el);
        }

        for (WebElement el: driver.findElements(By.cssSelector("#mCSB_14_container > div[class^='filter-list-item']"))) {
            Stoke = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            StokeSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Stoke);
            mCSB_12.put(StokeSt, el);
        }

        char ch = ' ';
        for (WebElement el: driver.findElements(By.cssSelector("#sortForm > div > div > div.filter-list-items > div[class^='filter-list-item']"))) {
            Filter = el.findElement(By.cssSelector("div > label > span"));
            try {
                arrow = el.findElement(By.cssSelector("div > label > i"));
                if (arrow.getAttribute("class").startsWith("icon icon__arrow--grey rotate")) {
                    ch = '+';
                } else { ch = '-'; }
            } catch (NoSuchElementException e) {}
            FilterSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Filter) + " " + ch;
            Filters.put(FilterSt, el);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();

        Search = driver.findElement(By.cssSelector("#catalogFilterSearch > input[type='text']"));
        Search.clear();
        JTextField search = new JTextField();
        search.setColumns(40);
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Search.sendKeys(search.getText());
                Search.submit();
                Update(frame, driver);
            }
        });
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.weightx = 0.5;
        bag.gridy = 0;
        bag.gridx = 0;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.bottom = 10;
        panel.add(search, bag);
        panel.revalidate();

        JComboBox BrandCB = addCB(frame, driver, mCSB_11);
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.gridy = 1;
        bag.gridx = 0;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        bag.insets.bottom = 10;
        panel.add(BrandCB, bag);
        panel.revalidate();

        JComboBox StokeCB;
        if (mCSB_14.keySet().size()!=0) {
            JComboBox LineCB = addCB(frame, driver, mCSB_13);
            bag.fill = GridBagConstraints.HORIZONTAL;
            bag.gridx = 1;
            bag.anchor = GridBagConstraints.WEST;
            bag.insets.right = 5;
            panel.add(LineCB, bag);
            panel.revalidate();

            StokeCB = addCB(frame, driver, mCSB_14);
        }
        else { StokeCB = addCB(frame, driver, mCSB_13); }
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.gridx = 1;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        panel.add(StokeCB, bag);
        panel.revalidate();

        JComboBox LandCB = addCB(frame, driver, mCSB_12);
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.gridy = 2;
        bag.gridx = 0;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        panel.add(LandCB, bag);
        panel.revalidate();

        JComboBox FilterCB = addCB(frame, driver, Filters);
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.gridx = 2;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        panel.add(FilterCB, bag);
        panel.revalidate();

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        frame.setPreferredSize(new Dimension(1000,250));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public JComboBox addCB(JFrame frame, WebDriver driver, Map<String, WebElement> map) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = "var object = arguments[0];"
                + "var theEvent = document.createEvent(\"MouseEvent\");"
                + "theEvent.initMouseEvent(\"click\", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "object.dispatchEvent(theEvent);";

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                WebElement ThisEl = map.get(item).findElement(By.cssSelector("label"));
                WebElement list = driver.findElement(By.cssSelector("#brand_autocomplete"));
                list.sendKeys(item);
                js.executeScript(script, ThisEl);
                Update(frame, driver);
            }
        };

        JComboBox CB = new JComboBox(map.keySet().toArray(new String[map.keySet().size()]));
        CB.addActionListener(actionListener);
        return CB;
    }
}
