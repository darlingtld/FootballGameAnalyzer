package lingda.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by lingda on 22/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GambleRatioCrawlerJufuImplTest {

    @Autowired
    @Qualifier("jufuCrawler")
    private GambleRatioCrawler gambleRatioCrawler;

    @Test
    public void shouldReturnGambleRatio() throws Exception {
        String page = gambleRatioCrawler.loginAndPDPage();
//        System.out.println(page.asXml());
    }

    @Test
    public void testGoogleSearch() throws InterruptedException {
        // Optional, if not specified, WebDriver will search your path for chromedriver.
        System.setProperty("webdriver.chrome.driver", "/Users/lingda/splunk/workspace/FootballGameAnalyzer/src/main/resources/chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.get("http://www.google.com/xhtml");
        Thread.sleep(5000);  // Let the user actually see something!
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("ChromeDriver");
        searchBox.submit();
        Thread.sleep(5000);  // Let the user actually see something!
        driver.quit();
    }
}
