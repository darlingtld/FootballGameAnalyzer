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
    private static final Integer POLLING_INTERVAL_MS = 100;

    public static WebElement returnOnFindingElement(WebDriver driver, By selector) {
        while (true) {
            try {
                logger.info("finding element by {}", selector.toString());
                WebElement element = driver.findElement(selector);
                logger.info("found element by {}", selector.toString());
                return element;
            } catch (NoSuchElementException e) {
                try {
                    Thread.sleep(POLLING_INTERVAL_MS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static List<WebElement> returnOnFindingElements(WebDriver driver, By selector) {
        while (true) {
            try {
                logger.info("finding element by {}", selector.toString());
                List<WebElement> elementList = driver.findElements(selector);
                logger.info("found element by {}", selector.toString());
                return elementList;
            } catch (NoSuchElementException e) {
                try {
                    Thread.sleep(POLLING_INTERVAL_MS);
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
                    Thread.sleep(POLLING_INTERVAL_MS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void returnOnFinishLoading(WebDriver driver, String loadingIndicator) {
        while (driver.getPageSource().contains(loadingIndicator)) {
            logger.info("waiting for page finish loading indicator={}", loadingIndicator);
            try {
                Thread.sleep(POLLING_INTERVAL_MS);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
