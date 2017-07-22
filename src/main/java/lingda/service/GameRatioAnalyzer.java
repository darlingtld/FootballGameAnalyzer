package lingda.service;

import lingda.model.GameRatio;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingda on 22/07/2017.
 */
@Service
public class GameRatioAnalyzer {

    public void analyze(List<GameRatio> jufuRatioList, List<GameRatio> hgaGameRatioList) {
        HashMap<String, GameRatio> hgaGameRatioMap = new HashMap<>();
        for (GameRatio gameRatio : hgaGameRatioList) {
            hgaGameRatioMap.put(gameRatio.getHomeTeam() + gameRatio.getAwayTeam(), gameRatio);
        }

        for (GameRatio jufuGameRatio : jufuRatioList) {
            GameRatio hgaGameRatio = hgaGameRatioMap.get(jufuGameRatio.getHomeTeam() + jufuGameRatio.getAwayTeam());
            if (hgaGameRatio == null) {
                continue;
            }

            for (Map.Entry<String, Double> jufuHomeTeamRatioEntry : jufuGameRatio.getHomeTeamRatioMap().entrySet()) {
                String condition = jufuHomeTeamRatioEntry.getKey();

            }
        }
    }
}
