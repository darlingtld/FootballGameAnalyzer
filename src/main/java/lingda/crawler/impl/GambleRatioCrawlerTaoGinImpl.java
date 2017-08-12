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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("taoginCrawler")
public class GambleRatioCrawlerTaoGinImpl implements GambleRatioCrawler {

    private static final Logger logger = LoggerFactory.getLogger(GambleRatioCrawlerTaoGinImpl.class);

    @Value("${gamble.website.taogin}")
    private String website;

    @Value("${gamble.website.taogin.username}")
    private String username;

    @Value("${gamble.website.taogin.password}")
    private String password;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final String HOMETEAM_REGEX = "(.*?)\\(主\\)";
    private static final Pattern HOMETEAM_PATTERN = Pattern.compile(HOMETEAM_REGEX);
    private static final String AWAYTEAM_REGEX = "v\\s+(.*?)\\s+";
    private static final Pattern AWAYTEAM_PATTERN = Pattern.compile(AWAYTEAM_REGEX);
    private static final String GAMETIME_REGEX = "(\\d{2}-\\d{2}.*)";
    private static final Pattern GAMETIME_PATTERN = Pattern.compile(GAMETIME_REGEX);

    @Override
    public List<String> loginAndPDPage() throws Exception {
        logger.info("trying to login to {}", website);
        System.setProperty("webdriver.chrome.driver", resourceLoader.getResource("classpath:chromedriver").getFile().getPath());
//        System.setProperty("webdriver.chrome.driver", "/Users/rita/Downloads/gamble/chromedriver");

        WebDriver driver = new ChromeDriver(new ChromeDriverService.Builder().withSilent(true).build());
        try {
            driver.get(website);
            Thread.sleep(2000);  // Let the user actually see something!
            WebElement form = driver.findElement(By.className("user_s"));
            form.findElement(By.name("account")).sendKeys(username);
            form.findElement(By.name("pwd")).sendKeys(password);
            driver.findElement(By.className("btn_login")).click();
            Thread.sleep(5000);  // Let the user actually see something!
//            String html = driver.getPageSource();
//            System.out.println(html);
            driver.switchTo().frame("leftmenu");
            WebElement gameListMenu = driver.findElement(By.id("cssmenu"));
            List<WebElement> liList = gameListMenu.findElement(By.className("open")).findElements(By.tagName("li"));
            List<String> htmlList = new ArrayList<>();
//            int count = 4;
            try {
                for (WebElement li : liList) {
//                    if (count-- < 0) {
//                        break;
//                    }
                    li.click();
                    Thread.sleep(2500);
                    driver.switchTo().parentFrame().switchTo().frame("rightmenu");
                    String bdHtml = driver.getPageSource();
//                    System.out.println(bdHtml);
                    htmlList.add(bdHtml);
                    driver.switchTo().parentFrame().switchTo().frame("leftmenu");
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
            try {
                Document doc = Jsoup.parse(html);
                Elements gameMetadata = doc.getElementById("marketname").getElementsByTag("td");
                Element pdTable = doc.getElementsByClass("rightlist").first().getElementsByTag("td").first();
//        make sure it is 波胆
                if (!pdTable.text().contains("波胆")) {
                    throw new RuntimeException("this page is not for 波胆");
                }
                GameRatio gameRatio = new GameRatio();
                String bothTeams = gameMetadata.get(1).getElementsByTag("b").first().text();
                String leagueName = gameMetadata.get(0).getElementsByTag("b").first().text();
                gameRatio.setLeague(leagueName);
                gameRatio.setHomeTeam(parseHomeTeam(bothTeams));
                gameRatio.setAwayTeam(parseAwayTeam(bothTeams));
                gameRatio.setGameTime(parseGameTime(bothTeams));
                Map<String, Double> homeTeamRatioMap = new LinkedHashMap<>();
                Map<String, Double> awayTeamRatioMap = new LinkedHashMap<>();
//            get 波胆全场
                Elements dataList = pdTable.getElementsByTag("tr");
                for (Element data : dataList.subList(2, dataList.size())) {
                    Element conditionEle = data.getElementsByTag("td").get(0);
                    if (!conditionEle.hasClass("openallcenter")) {
                        continue;
                    }
                    String condition = conditionEle.text().replace(" ", "");
                    String percent = data.getElementsByTag("td").get(1).text();
                    Double ratio = 0.0;
                    if (!"".equals(percent.trim())) {
                        try {
                            ratio = Double.parseDouble(percent.substring(0, percent.indexOf("%")));
                        } catch (StringIndexOutOfBoundsException e) {
                            ratio = 0.0;
                        }
                    }
                    homeTeamRatioMap.put(condition, ratio);
                    awayTeamRatioMap.put(condition, ratio);
                }
                gameRatio.setHomeTeamRatioMap(homeTeamRatioMap);
                gameRatio.setAwayTeamRatioMap(awayTeamRatioMap);
                gameRatioList.add(gameRatio);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
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
        try {
            String encodedString = URLEncoder.encode(bothTeams, "UTF-8");
            String text = URLDecoder.decode(encodedString.replace("%C2%A0", "+"), "UTF-8");
            Matcher m = AWAYTEAM_PATTERN.matcher(text);
            if (m.find()) {
                return m.group(1).trim();
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
