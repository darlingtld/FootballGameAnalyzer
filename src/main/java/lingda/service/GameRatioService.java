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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lingda on 22/07/2017.
 */
@Service
public class GameRatioService {

    private static final Logger logger = LoggerFactory.getLogger(GameRatioService.class);

    private final static String HGA = "hga";
    private final static String TAOGIN = "taogin";
    private final static String JUFU = "jufu";
    private final static String LIJI = "liji";

    public Map<String, List<GameRatio>> gameRatioCache = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("jufuCrawler")
    private GambleRatioCrawler gambleRatioJufuCrawler;

    @Autowired
    @Qualifier("hgaCrawler")
    private GambleRatioCrawler gambleRatioHgaCrawler;

    @Autowired
    @Qualifier("taoginCrawler")
    private GambleRatioCrawler gambleRatioTaoginCrawler;

    @Autowired
    @Qualifier("lijiCrawler")
    private GambleRatioCrawler gambleRatioLijiCrawler;

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
        if (gameRatioCache.containsKey(JUFU)) {
            logger.info("get {} game ratio list from cache", JUFU);
            return gameRatioCache.get(JUFU);
        }
        logger.info("get {} game ratio list from crawler", JUFU);
        List<String> page = gambleRatioJufuCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioJufuCrawler.getGameRatioByParsingHtml(page);
        logger.info("============ 钜富 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 钜富 end ============");
        gameRatioCache.put(JUFU, gameRatioList);
        return gameRatioList;
    }

    //    波胆
    public List<GameRatio> getRatioListFromHga() throws Exception {
        if (gameRatioCache.containsKey(HGA)) {
            logger.info("get {} game ratio list from cache", HGA);
            return gameRatioCache.get(HGA);
        }
        logger.info("get {} game ratio list from crawler", HGA);
        List<String> pdPage = gambleRatioHgaCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioHgaCrawler.getGameRatioByParsingHtml(pdPage);
        logger.info("============ 皇冠 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 皇冠 end ============");
        gameRatioCache.put(HGA, gameRatioList);
        return gameRatioList;
    }

    //    反波胆
    public List<GameRatio> getRatioListFromTaogin() throws Exception {
        if (gameRatioCache.containsKey(TAOGIN)) {
            logger.info("get {} game ratio list from cache", TAOGIN);
            return gameRatioCache.get(TAOGIN);
        }
        logger.info("get {} game ratio list from crawler", TAOGIN);
        List<String> pdPage = gambleRatioTaoginCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioTaoginCrawler.getGameRatioByParsingHtml(pdPage);
        logger.info("============ 淘金 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 淘金 end ============");
        gameRatioCache.put(TAOGIN, gameRatioList);
        return gameRatioList;
    }

    public List<GameRatio> getRatioListFromLiji() throws Exception {
        if (gameRatioCache.containsKey(LIJI)) {
            logger.info("get {} game ratio list from cache", LIJI);
            return gameRatioCache.get(LIJI);
        }
        logger.info("get {} game ratio list from crawler", LIJI);
        List<String> pdPage = gambleRatioLijiCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioLijiCrawler.getGameRatioByParsingHtml(pdPage);
        logger.info("============ 利记 ============");
        for (GameRatio gameRatio : gameRatioList) {
            logger.info(gameRatio.toString());
        }
        logger.info("============ 利记 end ============");
        gameRatioCache.put(LIJI, gameRatioList);
        return gameRatioList;
    }

    public void clearCache() {
        gameRatioCache.clear();
    }

}
