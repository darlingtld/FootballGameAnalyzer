package lingda.model;

import java.util.Map;

/**
 * Created by lingda on 22/07/2017.
 */
public class GameRatio {

    private String homeTeam;
    private String awayTeam;
    //    ratio map is comprised of <score, ratio>.  e.g. [<1-0, 4.5>, <1-1, 6.7>, ...]
    private Map<String, Double> ratioMap;

    @Override
    public String toString() {
        return "GameRatio{" +
                "homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", ratioMap=" + ratioMap +
                '}';
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Map<String, Double> getRatioMap() {
        return ratioMap;
    }

    public void setRatioMap(Map<String, Double> ratioMap) {
        this.ratioMap = ratioMap;
    }
}
