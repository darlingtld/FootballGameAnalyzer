package lingda.crawler;

import lingda.model.GameRatio;
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
import java.util.List;

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
        List<String> page = gambleRatioCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioCrawler.getGameRatioByParsingHtml(page);
        for(GameRatio gameRatio : gameRatioList){
            System.out.println(gameRatio);
        }
    }
}
