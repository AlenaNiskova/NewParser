package com.alena.Threads;

import com.alena.JSONWork;
import com.alena.Records.Review;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Texts implements Runnable {
    WebDriver driver;
    JSONWork jsonWork;

    public Texts(WebDriver driver, JSONWork jsonWork) {
        this.driver = driver;
        this.jsonWork = jsonWork;
    }

    public void run() {
        String a;
        Review review;
        List<WebElement> rating;
        List<WebElement> Next;
        List<Review> reviews = new ArrayList<Review>();
        String id = "";
        String text = "";
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (WebElement e : driver.findElements(By.className("product-detail__articul"))) {
            id = e.findElement(By.className("value")).getText();
        }
        for (WebElement e : driver.findElements(By.cssSelector("div.product-detail__top > h1"))) {
            text = e.getText();
        }
        for (WebElement e : driver.findElements(By.cssSelector("div.block-weight-info > ul > li"))) {
            text = text + e.getText() + "\n";
        }
        for (WebElement e : driver.findElements(By.cssSelector("div.truncate-text.detail-text > div"))) {
            text = text + (String) js.executeScript("var element = arguments[0];return element.textContent;", e) + "\n";
        }
        jsonWork.addText(id, text);
        String script = "var object = arguments[0];"
                + "var theEvent = document.createEvent(\"MouseEvent\");"
                + "theEvent.initMouseEvent(\"click\", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                + "object.dispatchEvent(theEvent);";
        js.executeScript(script, driver.findElement(By.id("prodReviewsTab")));
        String count = (String) js.executeScript("var element = arguments[0];return element.textContent;",
                driver.findElement(By.cssSelector("#prodReviewsTab > span")));
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(count);
        while (matcher.find()) {
            count = matcher.group();
        }
        int c;
        c = Integer.parseInt(count) + 4;
        while ((int) (c / 4) > 0) {
            for (WebElement e : driver.findElements(By.cssSelector("div.reviews-item"))) {
                review = new Review();
                rating = e.findElements(By.cssSelector("div.reviews-item__content > div.info > div > div.rating__item.active"));
                review.setRating(rating.size());
                a = "";
                for (WebElement name : e.findElements(By.cssSelector("div.reviews-item__head > div.title > span"))) {
                    a = a + (String) js.executeScript("var element = arguments[0];return element.textContent;", name) + "\n";
                }
                for (WebElement title : e.findElements(By.cssSelector("div.reviews-item__content > div.title"))) {
                    a = a + (String) js.executeScript("var element = arguments[0];return element.textContent;", title) + "\n";
                }
                for (WebElement revtext : e.findElements(By.cssSelector("div.reviews-item__content > div.text"))) {
                    a = a + (String) js.executeScript("var element = arguments[0];return element.textContent;", revtext);
                }
                review.setText(a);
                reviews.add(review);
            }
            Next = driver.findElements(By.cssSelector("#REPLIERttxbpagen > div.pagination > div > a"));
            if (Next.size()!=0) {
                if (driver.findElement(By.cssSelector("#REPLIERttxbpagen > div.pagination > a")).getText().startsWith("В КОНЕЦ")) {
                    js.executeScript(script, Next.get(0));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            c = c - 4;
        }
        jsonWork.addReview(id, reviews);
    }
}
