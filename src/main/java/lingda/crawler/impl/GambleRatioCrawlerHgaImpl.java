package lingda.crawler.impl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import lingda.crawler.GambleRatioCrawler;
import lingda.model.GameRatio;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

/**
 * Created by lingda on 22/07/2017.
 */
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
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setTimeout(20000);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setRedirectEnabled(true);

            final HtmlPage mainPage = webClient.getPage(website);
            final List<FrameWindow> window = mainPage.getFrames();
            final HtmlPage loginPage = (HtmlPage) window.get(0).getEnclosedPage();
            // Get the form that we are dealing with and within that form,
            // find the submit button and the field that we want to change.
            final HtmlForm form = loginPage.getFormByName("LoginForm");

            final HtmlTextInput usernameInput = (HtmlTextInput) form.getElementsByAttribute("input", "id", "username").get(0);
            usernameInput.setValueAttribute(username);
            final HtmlPasswordInput passwordInput = (HtmlPasswordInput) form.getElementsByAttribute("input", "id", "passwd").get(0);
            passwordInput.setValueAttribute(password);
            // Now submit the form by clicking the button and get back the second page.
            HtmlDivision button = (HtmlDivision) form.getElementsByAttribute("div", "class", "za_button").get(0);
            WebWindow loginPageEnclosingWindow = loginPage.getEnclosingWindow();
            button.click();
            while (loginPageEnclosingWindow.getEnclosedPage() == loginPage) {
                // The page hasn't changed.
                Thread.sleep(500);
            }

            /*    Logged In    */
            HtmlPage enclosedPage = (HtmlPage) loginPageEnclosingWindow.getParentWindow().getEnclosedPage();

            final FrameWindow gameHallWindow = enclosedPage.getFrameByName("SI2_mem_index");

            final HtmlPage gameHallPage = (HtmlPage) gameHallWindow.getEnclosedPage();

            Thread.sleep(1000);
//            System.out.println("game hall page : " + gameHallPage.asXml());

            final FrameWindow memOrderWindow = gameHallPage.getFrameByName("mem_order");

            Thread.sleep(1000);
            HtmlPage memOrderPage = (HtmlPage) memOrderWindow.getEnclosedPage();
//            System.out.println("mem order page : " + memOrderPage.asXml());

//            click on 波胆
            DomElement element = memOrderPage.getElementById("wtype_FT_pd");
            element.click();
            Thread.sleep(1000);

//            get the content after the click event
            final FrameWindow bodyWindow = gameHallPage.getFrameByName("body");

            Thread.sleep(1000);
            HtmlPage bodyPage = (HtmlPage) bodyWindow.getEnclosedPage();
//            System.out.println("body page : " + bodyPage.asXml());
            return Collections.singletonList(bodyPage.asXml());
        }
    }

    @Override
    public List<GameRatio> getGameRatioByParsingHtml(List<String> htmlList) {
        List<GameRatio> gameRatioList = new ArrayList<>();
        for (String html : htmlList) {
//        make sure it is 波胆
            if (!html.contains("波胆：全场")) {
                throw new RuntimeException("this page is not for 波胆：全场");
            }
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
