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

    public List<Bingo> analyze(List<GameRatio> taoginRatioList, List<GameRatio> hgaGameRatioList) {
        logger.info("start to analyze the ratio result between taogin and hga");
        HashMap<String, GameRatio> hgaGameRatioMap = new HashMap<>();
        for (GameRatio gameRatio : hgaGameRatioList) {
            hgaGameRatioMap.put(gameRatio.getHomeTeam() + gameRatio.getAwayTeam(), gameRatio);
        }

        List<Bingo> bingoList = new ArrayList<>();

        for (GameRatio taoginGameRatio : taoginRatioList) {
            logger.info("analyzing game {} vs {}", taoginGameRatio.getHomeTeam(), taoginGameRatio.getAwayTeam());
            GameRatio hgaGameRatio = hgaGameRatioMap.get(taoginGameRatio.getHomeTeam() + taoginGameRatio.getAwayTeam());
            if (hgaGameRatio == null) {
                logger.info("the game {} vs {} is not found in hga", taoginGameRatio.getHomeTeam(), taoginGameRatio.getAwayTeam());
                continue;
            }
            Bingo bingo = new Bingo();
            bingo.setHomeTeam(taoginGameRatio.getHomeTeam());
            bingo.setAwayTeam(taoginGameRatio.getAwayTeam());
            bingo.setLeague(taoginGameRatio.getLeague());
            bingo.setGameTime(hgaGameRatio.getGameTime());
            List<LuckyRatio> luckyRatioList = new ArrayList<>();
            bingo.setLuckyRatioList(luckyRatioList);
            for (Map.Entry<String, Double> taoginHomeTeamRatioEntry : taoginGameRatio.getHomeTeamRatioMap().entrySet()) {
                String condition = taoginHomeTeamRatioEntry.getKey();
                Double taoginPercent = taoginHomeTeamRatioEntry.getValue();
                Double hgaRatio = hgaGameRatio.getHomeTeamRatioMap().getOrDefault(condition, hgaGameRatio.getHomeTeamRatioMap().get("其他比分"));
                logger.info("[{}] [taogin]:{}% [hga]: {}", condition, taoginPercent, hgaRatio);
                LuckyRatio luckyRatio;
                Double ratioScore = getRatioScore(taoginPercent, hgaRatio);
                boolean isLuckyDraw = ratioScore >= 110;
                if (isLuckyDraw) {
                    logger.info("Bingo! {} vs {}, bet on {}, [hga]:{} [taogin]:{}%", taoginGameRatio.getHomeTeam(), taoginGameRatio.getAwayTeam(), condition, hgaRatio, taoginPercent);
                    luckyRatio = new LuckyRatio(condition, taoginPercent, hgaRatio, ratioScore, true);
                } else {
                    luckyRatio = new LuckyRatio(condition, taoginPercent, hgaRatio, ratioScore, false);
                }
                luckyRatioList.add(luckyRatio);
            }
            bingoList.add(bingo);
        }
        return bingoList;
    }

    private Double getRatioScore(Double taoginPercent, Double hgaGameRatio) {
//        double winningMoneyFromJufu = (jufuPercent / 100 + 1) * 0.95 - 1;
        logger.info("analyzer gives {}", taoginPercent * (hgaGameRatio - 1));
        return taoginPercent * (hgaGameRatio - 1);
//        return winningMoneyFromJufu * (hgaGameRatio - 1);
    }
}
