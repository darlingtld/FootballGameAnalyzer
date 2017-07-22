package lingda.controller;

import lingda.service.GambleRatioService;
import lingda.service.GameRatioAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lingda on 25/06/2017.
 */
@RestController
@RequestMapping("ratio")
public class GambleRatioController {

    @Autowired
    private GameRatioAnalyzer gameRatioAnalyzer;

    @Autowired
    private GambleRatioService gambleRatioService;

    @GetMapping
    public void getLuckyGames(){
        try {
            gameRatioAnalyzer.analyze(gambleRatioService.getRatioListFromJufu(), gambleRatioService.getRatioListFromHga());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
