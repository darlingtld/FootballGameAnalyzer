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

@Component("hgaCrawler")
public class GambleRatioCrawlerHgaImpl implements GambleRatioCrawler {

    private static final Logger logger = LoggerFactory.getLogger(GambleRatioCrawlerHgaImpl.class);

    @Value("${gamble.website.hga}")
    private String website;

    @Value("${gamble.website.hga.username}")
    private String username;

    @Value("${gamble.website.hga.password}")
    private String password;


    @Override
    public List<String> loginAndPDPage() throws Exception {
        logger.info("trying to login to {}", website);

        WebDriver driver = new ChromeDriver(new ChromeDriverService.Builder().withSilent(true).build());
        try {
            driver.get(website);
//            login
            driver.switchTo().frame("SI2_mem_index");
            WebElement ipEle = DriverUtils.returnOnFindingElement(driver, By.className("banner"));
            String ip = "http://" + ipEle.getText();
            logger.info("trying to login to {}", ip);
            driver = driver.switchTo().parentFrame();
            driver.get(ip);
            driver.switchTo().frame("SI2_mem_index");
            WebElement cnEle = DriverUtils.returnOnFindingElement(driver, By.id("lang_cn"));
            cnEle.click();
            WebElement form = DriverUtils.returnOnFindingElement(driver, By.id("LoginForm"));
            form.findElement(By.id("username")).sendKeys(username);
            form.findElement(By.id("passwd")).sendKeys(password);
            driver.findElement(By.className("za_button")).click();
            logger.info("user logged in");
//            click on 波胆
            driver = DriverUtils.returnOnFindingFrame(driver, "SI2_mem_index");
            driver = DriverUtils.returnOnFindingFrame(driver, "mem_order");
            WebElement bdEle = DriverUtils.returnOnFindingElement(driver, By.id("wtype_FT_pd"));
            bdEle.click();
            logger.info("get bo dan data");
            driver = DriverUtils.returnOnFindingFrame(driver.switchTo().parentFrame(), "body");
            return Collections.singletonList(driver.getPageSource());
        } finally {
            driver.quit();
        }
    }

    @Override
    public List<GameRatio> getGameRatioByParsingHtml(List<String> htmlList) {
        List<GameRatio> gameRatioList = new ArrayList<>();
        for (String html : htmlList) {
//        make sure it is 波胆
//            if (!html.contains("波胆：全场")) {
//                throw new RuntimeException("this page is not for 波胆：全场");
//            }
            Document doc = Jsoup.parse(html);
            Element showtable = doc.getElementById("showtable");
            Element titleTR = showtable.getElementById("title_tr");
            Elements elements = titleTR.getElementsByClass("bet_title_point");
//            for (Element ele : elements) {
//                System.out.println(ele.toString());
//            }

            Elements trElements = showtable.getElementsByTag("tr");
            for (int i = 0; i < trElements.size(); i++) {
                Element tr = trElements.get(i);
//        bet_game_league, bet_game_correct_top, bet_game_correct_other
                if (tr.hasClass("bet_game_league")) {
//                get the league name
                    GameRatio gameRatio = new GameRatio();
                    Map<String, Double> homeTeamRatioMap = new LinkedHashMap<>();
                    Map<String, Double> awayTeamRatioMap = new LinkedHashMap<>();
                    gameRatio.setLeague(tr.text());
//                get the home team ratio
                    Element home = trElements.get(i + 1);
                    parseRowData(home, gameRatio, homeTeamRatioMap, true);

//                get the away team ratio
                    Element away = trElements.get(i + 2);
                    parseRowData(away, gameRatio, awayTeamRatioMap, false);

                    postProcessRatioMapToAlignWithJufu(gameRatio, homeTeamRatioMap, awayTeamRatioMap);
                    gameRatioList.add(gameRatio);
                }
            }
        }
        return gameRatioList;
    }

    private void postProcessRatioMapToAlignWithJufu(GameRatio gameRatio, Map<String, Double> homeTeamRatioMap, Map<String, Double> awayTeamRatioMap) {
        for (Map.Entry<String, Double> ratio : awayTeamRatioMap.entrySet()) {
            if (!homeTeamRatioMap.containsKey(ratio.getKey())) {
                homeTeamRatioMap.put(ratio.getKey(), ratio.getValue());
            }
        }

        for (Map.Entry<String, Double> ratio : homeTeamRatioMap.entrySet()) {
            if (!awayTeamRatioMap.containsKey(ratio.getKey())) {
                awayTeamRatioMap.put(ratio.getKey(), ratio.getValue());
            }
        }

//        reverse the key in awayTeamRatioMap
        Map<String, Double> reversedAwayTeamRatioMap = new HashMap<>();
        for (Map.Entry<String, Double> ratio : awayTeamRatioMap.entrySet()) {
            if (ratio.getKey().contains("-")) {
                reversedAwayTeamRatioMap.put(new StringBuilder(ratio.getKey()).reverse().toString(), ratio.getValue());
            } else {
                reversedAwayTeamRatioMap.put(ratio.getKey(), ratio.getValue());
            }
        }
        gameRatio.setHomeTeamRatioMap(homeTeamRatioMap);
        gameRatio.setAwayTeamRatioMap(reversedAwayTeamRatioMap);
    }

    private void parseRowData(Element home, GameRatio gameRatio, Map<String, Double> teamRatioMap, Boolean isHome) {
        Elements homeElements = home.getElementsByTag("td");
        for (Element homeEle : homeElements) {
            if (homeEle.hasClass("bet_game_time")) {
                String gameTime = homeEle.text();
                gameRatio.setGameTime(gameTime);
            } else if (homeEle.hasClass("bet_team")) {
                String teamName = homeEle.text();
                if (isHome) {
                    gameRatio.setHomeTeam(teamName);
                } else {
                    gameRatio.setAwayTeam(teamName);
                }
            } else if (homeEle.hasClass("bet_text")) {
                teamRatioMap.put(homeEle.getElementsByAttribute("title").get(0).attr("title"), Double.parseDouble(homeEle.text()));
            } else if (homeEle.hasClass("bet_correct_bg")) {
                teamRatioMap.put(homeEle.getElementsByAttribute("title").get(0).attr("title"), Double.parseDouble(homeEle.text()));
            }
        }
    }
}
