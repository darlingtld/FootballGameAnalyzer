package lingda.crawler;

import lingda.model.GameRatio;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by lingda on 22/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GambleRatioCrawlerHgaImplTest {

    @Autowired
    @Qualifier("hgaCrawler")
    private GambleRatioCrawler gambleRatioCrawler;

    @Test
    public void shouldReturnGambleRatio() throws Exception {
        List<String> pdPage = gambleRatioCrawler.loginAndPDPage();
        List<GameRatio> gambleRatioList = gambleRatioCrawler.getGameRatioByParsingHtml(pdPage);
        for(GameRatio gameRatio: gambleRatioList){
            System.out.println(gameRatio);
        }
    }
}
