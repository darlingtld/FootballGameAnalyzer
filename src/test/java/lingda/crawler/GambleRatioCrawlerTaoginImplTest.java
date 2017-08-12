package lingda.crawler;

import lingda.model.GameRatio;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GambleRatioCrawlerTaoginImplTest {

    @Autowired
    @Qualifier("taoginCrawler")
    private GambleRatioCrawler gambleRatioCrawler;

    @Test
    public void shouldReturnGambleRatio() throws Exception {
        List<String> page = gambleRatioCrawler.loginAndPDPage();
        List<GameRatio> gameRatioList = gambleRatioCrawler.getGameRatioByParsingHtml(page);
        for(GameRatio gameRatio : gameRatioList){
            System.out.println(gameRatio);
        }
    }
}
