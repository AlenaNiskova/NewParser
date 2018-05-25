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
import java.util.List;
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
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel loading = new JPanel();
        loading.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();
        JLabel load = new JLabel("Ожидайте загрузки...");
        bag.gridy = 0;
        bag.gridx = 0;
        bag.insets.right = 10;
        loading.add(load, bag);
        loading.revalidate();
        bag.gridx = 1;
        loading.add(progressBar, bag);
        loading.revalidate();
        frame.getContentPane().removeAll();
        frame.getContentPane().add(loading);

        frame.setPreferredSize(new Dimension(1000,700));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        WebElement Brand, Land, Line, Search;
        String BrandSt, LandSt, LineSt;
        List<WebElement> brandFilt, landFilt, lineFilt;

        Map<String, WebElement> Brands = new LinkedHashMap<>();
        Map<String, WebElement> Lands = new LinkedHashMap<>();
        Map<String, WebElement> Lines = new LinkedHashMap<>();

        WebElement dynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body > div.page-wrapper > header > div.header-logo")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = "var object = arguments[0];"
                + "var theEvent = document.createEvent(\"MouseEvent\");"
                + "theEvent.initMouseEvent(\"click\", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "object.dispatchEvent(theEvent);";

        brandFilt = driver.findElements(By.cssSelector("#mCSB_11_container > div[class^='filter-list-item']"));
        for (WebElement el: brandFilt) {
            Brand = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            BrandSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Brand);
            Brands.put(BrandSt, el);
        }

        lineFilt = driver.findElements(By.cssSelector("#mCSB_12_container > div[class^='filter-list-item']"));
        for (WebElement el: lineFilt) {
            Line = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            LineSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Line);
            Lines.put(LineSt, el);
        }

        landFilt = driver.findElements(By.cssSelector("#mCSB_13_container > div[class^='filter-list-item']"));
        for (WebElement el: landFilt) {
            Land = el.findElement(By.cssSelector("div > label > span.filter-value-title"));
            LandSt = (String) js.executeScript("var element = arguments[0];return element.textContent;", Land);
            Lands.put(LandSt, el);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        bag = new GridBagConstraints();

        ActionListener BrandactionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                WebElement ThisEl = Brands.get(item).findElement(By.cssSelector("label"));
                WebElement list = driver.findElement(By.cssSelector("#brand_autocomplete"));
                list.sendKeys(item);
                js.executeScript(script, ThisEl);
                Update(frame, driver);
            }
        };

        ActionListener LineactionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                WebElement ThisEl = Lines.get(item).findElement(By.cssSelector("label"));
                WebElement list = driver.findElement(By.cssSelector("#line_autocomplete[value^='Все линейки']"));
                list.sendKeys(item);
                js.executeScript(script, ThisEl);
                Update(frame, driver);
            }
        };

        ActionListener LandactionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                WebElement ThisEl = Lands.get(item).findElement(By.cssSelector("label"));
                WebElement list = driver.findElement(By.cssSelector("#line_autocomplete[value^='Все страны']"));
                list.sendKeys(item);
                js.executeScript(script, ThisEl);
                Update(frame, driver);
            }
        };

        Search = driver.findElement(By.cssSelector("#catalogFilterSearch > input[type='text']"));
        JTextField search = new JTextField();
        search.setColumns(45);
        //search.setToolTipText("Название товара или артикул");
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Search.sendKeys(search.getText());
                Search.submit();
                Update(frame, driver);
            }
        });
        bag.gridy = 0;
        bag.gridx = 0;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.bottom = 10;
        panel.add(search, bag);
        panel.revalidate();

        JComboBox BrandCB = new JComboBox(Brands.keySet().toArray(new String[Brands.keySet().size()]));
        BrandCB.addActionListener(BrandactionListener);
        bag.gridy = 1;
        bag.gridx = 0;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        panel.add(BrandCB, bag);
        panel.revalidate();

        JComboBox LineCB = new JComboBox(Lines.keySet().toArray(new String[Lines.keySet().size()]));
        LineCB.addActionListener(LineactionListener);
        bag.gridx = 1;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        panel.add(LineCB, bag);
        panel.revalidate();

        JComboBox LandCB = new JComboBox(Lands.keySet().toArray(new String[Lands.keySet().size()]));
        LandCB.addActionListener(LandactionListener);
        bag.gridx = 2;
        bag.anchor = GridBagConstraints.WEST;
        bag.insets.right = 5;
        panel.add(LandCB, bag);
        panel.revalidate();

        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);

        frame.setPreferredSize(new Dimension(1000,700));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
