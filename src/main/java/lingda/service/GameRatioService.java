package lingda.service;

import lingda.crawler.GambleRatioCrawler;
import lingda.model.GameRatio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lingda on 22/07/2017.
 */
@Service
public class GameRatioService {

    @Autowired
    @Qualifier("jufuCrawler")
    private GambleRatioCrawler gambleRatioJufuCrawler;

    @Autowired
    @Qualifier("hgaCrawler")
    private GambleRatioCrawler gambleRatioHgaCrawler;

    public List<GameRatio> getRatioListFromJufu() throws Exception {
        List<String> page = gambleRatioJufuCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioJufuCrawler.getGameRatioByParsingHtml(page);
//        for(GameRatio gameRatio : gameRatioList){
//            System.out.println(gameRatio);
//        }
        return gameRatioList;
    }


    public List<GameRatio> getRatioListFromHga() throws Exception {
        List<String> pdPage = gambleRatioHgaCrawler.loginAndPDPage();
        List<GameRatio> gambleRatioList = gambleRatioHgaCrawler.getGameRatioByParsingHtml(pdPage);
//        for (GameRatio gameRatio : gambleRatioList) {
//            System.out.println(gameRatio);
//        }
        return gambleRatioList;
    }
}
