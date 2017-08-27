package lingda.crawler.impl;

import lingda.crawler.GambleRatioCrawler;
import lingda.model.GameRatio;
import lingda.util.DriverUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("lijiCrawler")
public class GambleRatioCrawlerLijiImpl implements GambleRatioCrawler {

    private static final Logger logger = LoggerFactory.getLogger(GambleRatioCrawlerLijiImpl.class);

    @Value("${gamble.website.liji}")
    private String website;

    @Value("${gamble.website.liji.username}")
    private String username;

    @Value("${gamble.website.liji.password}")
    private String password;


    @Override
    public List<String> loginAndPDPage() throws Exception {
        logger.info("trying to login to {}", website);

        WebDriver driver = new ChromeDriver(new ChromeDriverService.Builder().withSilent(true).build());
        try {
            driver.get(website + "zh-cn/euro/混合过关");
            WebElement element = DriverUtils.returnOnFindingElement(driver, By.className("SelMarket"));
            element.findElement(By.tagName("a")).click();
            WebElement marketPanel = DriverUtils.returnOnFindingElement(driver, By.className("SelMarketT"));
            List<WebElement> tdElement = marketPanel.findElements(By.tagName("td"));
            WebElement bdCheckBox = getBdCheckBox(tdElement);
            bdCheckBox.findElement(By.tagName("input")).click();
            Thread.sleep(1000);
            return Collections.singletonList(driver.getPageSource());
        } finally {
            driver.quit();
        }
    }

    private WebElement getBdCheckBox(List<WebElement> tdElement) {
        for (WebElement td : tdElement) {
            if (td.getText().contains("波胆")) {
                return td;
            }
        }
        return null;
    }

    @Override
    public List<GameRatio> getGameRatioByParsingHtml(List<String> htmlList) {
        List<GameRatio> gameRatioList = new ArrayList<>();
        for (String html : htmlList) {
//        make sure it is 波胆
            Document doc = Jsoup.parse(html);
            Element marketEle = doc.getElementsByClass("MarketBd").get(0);
            Elements metaEles = marketEle.getElementsByClass("MarketLea");
            Elements dataEles = marketEle.getElementsByClass("Odds3Cols");

            for (int i = 0; i < metaEles.size(); i++) {
                Element meta = metaEles.get(i);
                Element data = dataEles.get(i);
//                get the league name
                String league = meta.getElementsByClass("SubHeadT").text();
                GameRatio gameRatio = new GameRatio();
                Map<String, Double> homeTeamRatioMap = new LinkedHashMap<>();
                Map<String, Double> awayTeamRatioMap = new LinkedHashMap<>();
                gameRatio.setLeague(league);
//                get the game time
                String gameTime = data.getElementsByTag("thead").get(0).getElementsByClass("DateTimeTxt").text();
                gameRatio.setGameTime(gameTime);
                String bothTeam = data.getElementsByTag("thead").get(0).getElementsByClass("EventName").text();
//                get the home team ratio
                String homeTeamName = bothTeam.split("\\s+")[0];
                String awayTeamName = bothTeam.split("\\s+")[2];
                gameRatio.setHomeTeam(homeTeamName);
                gameRatio.setAwayTeam(awayTeamName);
                Element ratioData = data.getElementsByTag("tbody").get(1);
                parseRatioData(ratioData, gameRatio, homeTeamRatioMap);

                processAwayTeamRatioData(gameRatio, homeTeamRatioMap, awayTeamRatioMap);
                gameRatioList.add(gameRatio);
            }
        }
        return gameRatioList;
    }

    private void parseRatioData(Element ratioData, GameRatio gameRatio, Map<String, Double> homeTeamRatioMap) {
        Elements aList = ratioData.getElementsByTag("a");
        for (Element a : aList) {
            Double odds = Double.parseDouble(a.getElementsByClass("OddsR").text());
            String score = a.getElementsByClass("OddsL").text().replace(" ", "");
            homeTeamRatioMap.put(score, odds);
            gameRatio.setHomeTeamRatioMap(homeTeamRatioMap);
        }
    }

    private void processAwayTeamRatioData(GameRatio gameRatio, Map<String, Double> homeTeamRatioMap, Map<String, Double> awayTeamRatioMap) {
//        reverse the key in awayTeamRatioMap
        for (Map.Entry<String, Double> ratio : homeTeamRatioMap.entrySet()) {
            if (ratio.getKey().contains("-")) {
                awayTeamRatioMap.put(new StringBuilder(ratio.getKey()).reverse().toString(), ratio.getValue());
            } else {
                awayTeamRatioMap.put(ratio.getKey(), ratio.getValue());
            }
        }
        gameRatio.setAwayTeamRatioMap(awayTeamRatioMap);
    }

}
