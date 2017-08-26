package lingda.service;

import lingda.model.Bingo;
import lingda.model.GameRatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingda on 23/07/2017.
 */
@Service
public class GameRatioResultContainer {

    private static final Logger logger = LoggerFactory.getLogger(GameRatioResultContainer.class);

    @Autowired
    private GameRatioAnalyzer gameRatioAnalyzer;

    @Autowired
    private GameRatioService gameRatioService;

    private final static long SECOND = 1000;
//    private List<Bingo> bingoList = null;

    private Map<String, List<Bingo>> map = new HashMap<>();

    public Map<String, List<Bingo>> getResultMap() {
        return map;
    }

//    public List<Bingo> getBingoList() {
//        if (this.bingoList == null) {
//            return Collections.emptyList();
//        } else {
//            return this.bingoList;
//        }
//    }

    //    分析波胆vs反波胆
    @Scheduled(fixedRate = SECOND * 30)
    public void analyzeHgaAndTaogin() {
        logger.info("start a job to fetch ratio and analyze at {}", LocalDateTime.now());
        try {
            List<GameRatio> hgaGameRatioList = gameRatioService.getRatioListFromHga();
            if (hgaGameRatioList == null || hgaGameRatioList.isEmpty()) {
                return;
            }
            List<GameRatio> taoginGameRatioList = gameRatioService.getRatioListFromTaogin();
            if (taoginGameRatioList == null || taoginGameRatioList.isEmpty()) {
                return;
            }
            List<Bingo> bingoList = gameRatioAnalyzer.analyze(taoginGameRatioList, hgaGameRatioList);
            map.put("皇冠vs淘金", bingoList);
            logger.info("job finished at {}", LocalDateTime.now());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = SECOND * 30)
    public void analyzeHgaAndJufu() {
        logger.info("start a job to fetch ratio and analyze at {}", LocalDateTime.now());
        try {
            List<GameRatio> hgaGameRatioList = gameRatioService.getRatioListFromHga();
            if (hgaGameRatioList == null || hgaGameRatioList.isEmpty()) {
                return;
            }
            List<GameRatio> jufuGameRatioList = gameRatioService.getRatioListFromJufu();
            if (jufuGameRatioList == null || jufuGameRatioList.isEmpty()) {
                return;
            }
            List<Bingo> bingoList = gameRatioAnalyzer.analyze(jufuGameRatioList, hgaGameRatioList);
            map.put("皇冠vs钜富", bingoList);
            logger.info("job finished at {}", LocalDateTime.now());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
