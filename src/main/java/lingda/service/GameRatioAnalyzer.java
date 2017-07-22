package lingda.service;

import lingda.model.GameRatio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingda on 22/07/2017.
 */
@Service
public class GameRatioAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(GameRatioAnalyzer.class);

    public void analyze(List<GameRatio> jufuRatioList, List<GameRatio> hgaGameRatioList) {
        logger.info("start to analyze the ratio result between jufu and hga");
        HashMap<String, GameRatio> hgaGameRatioMap = new HashMap<>();
        for (GameRatio gameRatio : hgaGameRatioList) {
            hgaGameRatioMap.put(gameRatio.getHomeTeam() + gameRatio.getAwayTeam(), gameRatio);
        }

        for (GameRatio jufuGameRatio : jufuRatioList) {
            logger.info("analyzing game {} vs {}", jufuGameRatio.getHomeTeam(), jufuGameRatio.getAwayTeam());
            GameRatio hgaGameRatio = hgaGameRatioMap.get(jufuGameRatio.getHomeTeam() + jufuGameRatio.getAwayTeam());
            if (hgaGameRatio == null) {
                logger.info("the game {} vs {} is not found in hga", jufuGameRatio.getHomeTeam(), jufuGameRatio.getAwayTeam());
                continue;
            }
            for (Map.Entry<String, Double> jufuHomeTeamRatioEntry : jufuGameRatio.getHomeTeamRatioMap().entrySet()) {
                String condition = jufuHomeTeamRatioEntry.getKey();
                Double jufuPercent = jufuHomeTeamRatioEntry.getValue();
                Double hgaRatio = hgaGameRatio.getHomeTeamRatioMap().getOrDefault(condition, hgaGameRatio.getHomeTeamRatioMap().get("其他比分"));
                logger.info("[{}] [jufu]:{}% [hga]: {}", condition, jufuPercent, hgaRatio);
                if (isLuckyDraw(jufuPercent, hgaRatio)) {
                    logger.info("Bingo! {} vs {}, bet on {}, [hga]:{} [jufu]:{}%", jufuGameRatio.getHomeTeam(), jufuGameRatio.getAwayTeam(), condition, hgaRatio, jufuPercent);
                }
            }
        }
    }

    private boolean isLuckyDraw(Double jufuPercent, Double hgaGameRatio) {
        double winningMoneyFromJufu = (jufuPercent / 100 + 1) * 0.95 - 1;
        logger.info("analyzer gives {}", winningMoneyFromJufu * (hgaGameRatio - 1));
        return winningMoneyFromJufu * (hgaGameRatio - 1) > 1;
    }
}
