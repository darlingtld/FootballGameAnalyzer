package lingda.crawler.impl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lingda.crawler.GambleRatioCrawler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by lingda on 22/07/2017.
 */
@Component("jufuCrawler")
public class GambleRatioCrawlerJufuImpl implements GambleRatioCrawler {

    private static final Logger logger = LoggerFactory.getLogger(GambleRatioCrawlerHgaImpl.class);

    @Value("${gamble.website.jufu}")
    private String website;

    @Value("${gamble.website.jufu.username}")
    private String username;

    @Value("${gamble.website.jufu.password}")
    private String password;

    @Override
    public String loginAndPDPage() throws Exception {
        logger.info("trying to login to {}", website);
        System.setProperty("webdriver.chrome.driver", "/Users/lingda/splunk/workspace/FootballGameAnalyzer/src/main/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(website);
        Thread.sleep(3000);  // Let the user actually see something!
        WebElement form = driver.findElement(By.className("logout_group"));
        form.findElement(By.name("username")).sendKeys(username);
        form.findElement(By.name("password")).sendKeys(password);
        form.findElement(By.className("btn_login")).click();
        Thread.sleep(3000);  // Let the user actually see something!
        String html = driver.getPageSource();
        driver.quit();
        return html;
    }
}
