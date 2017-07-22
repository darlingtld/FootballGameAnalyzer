package lingda.crawler.impl;

import lingda.crawler.GambleRatioCrawler;
import lingda.model.GameRatio;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String HOMETEAM_REGEX = "(.*?)\\(主\\)";
    private static final Pattern HOMETEAM_PATTERN = Pattern.compile(HOMETEAM_REGEX);
    private static final String AWAYTEAM_REGEX = "v(.*?)\\|";
    private static final Pattern AWAYTEAM_PATTERN = Pattern.compile(AWAYTEAM_REGEX);
    private static final String GAMETIME_REGEX = "\\|(.*)";
    private static final Pattern GAMETIME_PATTERN = Pattern.compile(GAMETIME_REGEX);

    @Override
    public List<String> loginAndPDPage() throws Exception {
        logger.info("trying to login to {}", website);
        System.setProperty("webdriver.chrome.driver", "/Users/lingda/splunk/workspace/FootballGameAnalyzer/src/main/resources/chromedriver");
        WebDriver driver = new ChromeDriver(new ChromeDriverService.Builder().withSilent(true).build());
        try {
            driver.get(website);
            Thread.sleep(3000);  // Let the user actually see something!
            WebElement form = driver.findElement(By.className("logout_group"));
            form.findElement(By.name("username")).sendKeys(username);
            form.findElement(By.name("password")).sendKeys(password);
            form.findElement(By.className("btn_login")).click();
            Thread.sleep(3000);  // Let the user actually see something!
//        String html = driver.getPageSource();
            WebElement gameListMenu = driver.findElement(By.className("game_list_menu"));
            List<WebElement> ulList = gameListMenu.findElements(By.tagName("ul"));
            WebElement activeGamesAll = null;
            for (WebElement webElement : ulList) {
                if (webElement.findElement(By.tagName("li")).findElement(By.tagName("span")).getText().contains("全部赛事")) {
                    activeGamesAll = webElement;
                    break;
                }
            }
            List<WebElement> accordionList = activeGamesAll.findElements(By.className("accordion_show"));
            List<String> htmlList = new ArrayList<>();
//            int count = 2;
            try {
                for (WebElement accordion : accordionList) {
//                    if (count-- < 0) {
//                        break;
//                    }
                    accordion.click();
                    Thread.sleep(1000);
                    htmlList.add(driver.getPageSource());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return htmlList;
        } finally {
            driver.quit();
        }
    }

    @Override
    public List<GameRatio> getGameRatioByParsingHtml(List<String> htmlList) {
        List<GameRatio> gameRatioList = new ArrayList<>();
        for (String html : htmlList) {
//        make sure it is 波胆
            if (!html.contains("波胆")) {
                throw new RuntimeException("this page is not for 波胆：全场");
            }
            GameRatio gameRatio = new GameRatio();
            Document doc = Jsoup.parse(html);
            Element element = doc.getElementsByClass("game_list_group").get(0);
            String bothTeams = element.getElementsByClass("team").text();
            String leagueName = element.getElementsByTag("h1").text();
            gameRatio.setLeague(leagueName);
            gameRatio.setHomeTeam(parseHomeTeam(bothTeams));
            gameRatio.setAwayTeam(parseAwayTeam(bothTeams));
            gameRatio.setGameTime(parseGameTime(bothTeams));
            Map<String, Double> homeTeamRatioMap = new LinkedHashMap<>();
            Map<String, Double> awayTeamRatioMap = new LinkedHashMap<>();
//            get 波胆全场
            Element bdElement = element.getElementsByClass("gl_content").get(0);
            Elements dataList = bdElement.getElementsByClass("glc_data");
            for (Element data : dataList) {
                String condition = data.getElementsByClass("data_1").text();
                String percent = data.getElementsByClass("data_2").text();
                Double ratio = Double.parseDouble(percent.substring(0, percent.indexOf("%")));
                homeTeamRatioMap.put(condition, ratio);
                awayTeamRatioMap.put(condition, ratio);
            }
            gameRatio.setHomeTeamRatioMap(homeTeamRatioMap);
            gameRatio.setAwayTeamRatioMap(awayTeamRatioMap);
            gameRatioList.add(gameRatio);
        }

        return gameRatioList;
    }

    private String parseGameTime(String bothTeams) {
        Matcher m = GAMETIME_PATTERN.matcher(bothTeams);
        if (m.find()) {
            return m.group(1).trim();
        } else {
            return "";
        }
    }

    private String parseAwayTeam(String bothTeams) {
        Matcher m = AWAYTEAM_PATTERN.matcher(bothTeams);
        if (m.find()) {
            return m.group(1).trim();
        } else {
            return "";
        }
    }

    private String parseHomeTeam(String bothTeams) {
        Matcher m = HOMETEAM_PATTERN.matcher(bothTeams);
        if (m.find()) {
            return m.group(1).trim();
        } else {
            return "";
        }
    }
}
