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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public String loginAndPDPage() throws Exception {
        logger.info("trying to login to {}", website);
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
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
            System.out.println("game hall page : " + gameHallPage.asXml());

            final FrameWindow memOrderWindow = gameHallPage.getFrameByName("mem_order");

            Thread.sleep(1000);
            HtmlPage memOrderPage = (HtmlPage) memOrderWindow.getEnclosedPage();
            System.out.println("mem order page : " + memOrderPage.asXml());

//            click on 波胆
            DomElement element = memOrderPage.getElementById("wtype_FT_pd");
            element.click();
            Thread.sleep(1000);

//            get the content after the click event
            final FrameWindow bodyWindow = gameHallPage.getFrameByName("body");

            Thread.sleep(1000);
            HtmlPage bodyPage = (HtmlPage) bodyWindow.getEnclosedPage();
//            System.out.println("body page : " + bodyPage.asXml());
            return bodyPage.asXml();
        }
    }

    public List<GameRatio> getGameRatioByParsingHtml(String html) {
        return null;
    }
}
