package lingda.service;

import lingda.crawler.GambleRatioCrawler;
import lingda.model.GameRatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * Created by lingda on 22/07/2017.
 */
@Service
public class GameRatioService {

    private static final Logger logger = LoggerFactory.getLogger(GameRatioService.class);

    @Autowired
    @Qualifier("jufuCrawler")
    private GambleRatioCrawler gambleRatioJufuCrawler;

    @Autowired
    @Qualifier("hgaCrawler")
    private GambleRatioCrawler gambleRatioHgaCrawler;

    @Autowired
    @Qualifier("taoginCrawler")
    private GambleRatioCrawler gambleRatioTaoginCrawler;

    @Value("${development}")
    private boolean isDevelopment;

    @Value("${production.chromedriver.location}")
    private String chromeDriverLocation;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    private void postConstruct() throws IOException {
        if (isDevelopment) {
            System.setProperty("webdriver.chrome.driver", resourceLoader.getResource("classpath:chromedriver").getFile().getPath());
        } else {
            System.setProperty("webdriver.chrome.driver", chromeDriverLocation);
        }
    }

    //    反波胆
    public List<GameRatio> getRatioListFromJufu() throws Exception {
        List<String> page = gambleRatioJufuCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioJufuCrawler.getGameRatioByParsingHtml(page);
        logger.info("============ 钜富 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 钜富 end ============");
        return gameRatioList;
    }

    //    波胆
    public List<GameRatio> getRatioListFromHga() throws Exception {
        List<String> pdPage = gambleRatioHgaCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioHgaCrawler.getGameRatioByParsingHtml(pdPage);
        logger.info("============ 皇冠 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 皇冠 end ============");
        return gameRatioList;
    }

    //    反波胆
    public List<GameRatio> getRatioListFromTaogin() throws Exception {
        List<String> pdPage = gambleRatioTaoginCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioTaoginCrawler.getGameRatioByParsingHtml(pdPage);
        logger.info("============ 淘金 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 淘金 end ============");
        return gameRatioList;
    }
}
