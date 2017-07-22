package lingda.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        String page = gambleRatioCrawler.loginAndPDPage();
//        System.out.println(page.asXml());
    }
}
