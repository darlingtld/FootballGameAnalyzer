package lingda.controller;

import lingda.model.Bingo;
import lingda.service.GameRatioResultContainer;
import lingda.service.GameRatioService;
import lingda.service.GameRatioAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Created by lingda on 25/06/2017.
 */
@RestController
@RequestMapping("ratio")
public class GambleRatioController {

    @Autowired
    private GameRatioResultContainer gameRatioResultContainer;

    @GetMapping
    public List<Bingo> getLuckyGames(){
        try {
            return gameRatioResultContainer.getBingoList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

}
