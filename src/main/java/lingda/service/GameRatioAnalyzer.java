package lingda.service;

import lingda.model.Bingo;
import lingda.model.GameRatio;
import lingda.model.LuckyRatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingda on 22/07/2017.
 */
@Service
public class GameRatioAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(GameRatioAnalyzer.class);

    public List<Bingo> analyze(List<GameRatio> fanbodanRatioList, List<GameRatio> bodanGameRatioList) {
        logger.info("start to analyze the ratio result between fanbodan and bodan");
        HashMap<String, GameRatio> bodanGameRatioMap = new HashMap<>();
        for (GameRatio gameRatio : bodanGameRatioList) {
//            hgaGameRatioMap.put(gameRatio.getHomeTeam() + gameRatio.getAwayTeam(), gameRatio);
            bodanGameRatioMap.put(gameRatio.getHomeTeam(), gameRatio);
        }

        List<Bingo> bingoList = new ArrayList<>();

        for (GameRatio fanbodanGameRatio : fanbodanRatioList) {
            logger.info("analyzing game {} vs {}", fanbodanGameRatio.getHomeTeam(), fanbodanGameRatio.getAwayTeam());
//            GameRatio hgaGameRatio = hgaGameRatioMap.get(taoginGameRatio.getHomeTeam() + taoginGameRatio.getAwayTeam());
            GameRatio bodanGameRatio = bodanGameRatioMap.get(fanbodanGameRatio.getHomeTeam());

            if (bodanGameRatio == null) {
                logger.info("the game {} vs {} is not found in bodan", fanbodanGameRatio.getHomeTeam(), fanbodanGameRatio.getAwayTeam());
                continue;
            }
            Bingo bingo = new Bingo();
            bingo.setHomeTeam(fanbodanGameRatio.getHomeTeam());
            bingo.setAwayTeam(fanbodanGameRatio.getAwayTeam());
            bingo.setLeague(fanbodanGameRatio.getLeague());
            bingo.setGameTime(bodanGameRatio.getGameTime());
            List<LuckyRatio> luckyRatioList = new ArrayList<>();
            bingo.setLuckyRatioList(luckyRatioList);
            for (Map.Entry<String, Double> fanbodanHomeTeamRatioEntry : fanbodanGameRatio.getHomeTeamRatioMap().entrySet()) {
                String condition = fanbodanHomeTeamRatioEntry.getKey();
                Double fanbodanPercent = fanbodanHomeTeamRatioEntry.getValue();
                Double bodanRatio;
                if (bodanGameRatio.getHomeTeamRatioMap().containsKey(condition)) {
                    bodanRatio = bodanGameRatio.getHomeTeamRatioMap().get(condition);
                } else if (bodanGameRatio.getHomeTeamRatioMap().containsKey("其他比分")) {
                    bodanRatio = bodanGameRatio.getHomeTeamRatioMap().get("其他比分");
                } else if (bodanGameRatio.getHomeTeamRatioMap().containsKey("其它")) {
                    bodanRatio = bodanGameRatio.getHomeTeamRatioMap().get("其它");
                } else {
                    bodanRatio = 0.0;
                }
                logger.info("[{}] [fanbodan]:{}% [bodan]: {}", condition, fanbodanPercent, bodanRatio);
                LuckyRatio luckyRatio;
                Double ratioScore = getRatioScore(fanbodanPercent, bodanRatio);
                boolean isLuckyDraw = ratioScore >= 110;
                if (isLuckyDraw) {
                    logger.info("Bingo! {} vs {}, bet on {}, [bodan]:{} [fanbodan]:{}%", fanbodanGameRatio.getHomeTeam(), fanbodanGameRatio.getAwayTeam(), condition, bodanRatio, fanbodanPercent);
                    luckyRatio = new LuckyRatio(condition, fanbodanPercent, bodanRatio, ratioScore, true);
                } else {
                    luckyRatio = new LuckyRatio(condition, fanbodanPercent, bodanRatio, ratioScore, false);
                }
                luckyRatioList.add(luckyRatio);
            }
            bingoList.add(bingo);
        }
        return bingoList;
    }

    private Double getRatioScore(Double fanbodanPercent, Double bodanGameRatio) {
//        double winningMoneyFromJufu = (jufuPercent / 100 + 1) * 0.95 - 1;
        logger.info("analyzer gives {}", fanbodanPercent * (bodanGameRatio - 1));
        return fanbodanPercent * (bodanGameRatio - 1);
//        return winningMoneyFromJufu * (hgaGameRatio - 1);
    }
}
