package lingda.crawler;

import lingda.model.GameRatio;

import java.util.List;

/**
 * Created by lingda on 22/07/2017.
 */
public interface GambleRatioCrawler {

//    登入并获取波胆页面
    List<String> loginAndPDPage() throws Exception;

//    解析页面并获取赔率数据
    List<GameRatio> getGameRatioByParsingHtml(List<String> html);
}
