package lingda.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DriverUtils {
    private static final Logger logger = LoggerFactory.getLogger(DriverUtils.class);

    public static WebElement returnOnFindingElement(WebDriver driver, By selector) {
        while (true) {
            try {
                logger.info("finding element by {}", selector.toString());
                WebElement element = driver.findElement(selector);
                logger.info("found element by {}", selector.toString());
                return element;
            } catch (NoSuchElementException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static WebDriver returnOnFindingFrame(WebDriver driver, String frame) {
        while (true) {
            try {
                logger.info("finding frame by name={}", frame);
                WebDriver webDriver = driver.switchTo().frame(frame);
                logger.info("found frame by {}", frame);
                return webDriver;
            } catch (NoSuchFrameException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
