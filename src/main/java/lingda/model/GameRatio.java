package lingda.model;

import java.util.Map;

/**
 * Created by lingda on 22/07/2017.
 */
public class GameRatio {

    private String homeTeam;
    private String awayTeam;
    private String league;
    private String gameTime;
    //    ratio map is comprised of <score, ratio>.  e.g. [<1-0, 4.5>, <1-1, 6.7>, ...]
    private Map<String, Double> homeTeamRatioMap;
    private Map<String, Double> awayTeamRatioMap;

    @Override
    public String toString() {
        return "GameRatio{" +
                "homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", league='" + league + '\'' +
                ", gameTime='" + gameTime + '\'' +
                ", homeTeamRatioMap=" + homeTeamRatioMap +
                ", awayTeamRatioMap=" + awayTeamRatioMap +
                '}';
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public Map<String, Double> getHomeTeamRatioMap() {
        return homeTeamRatioMap;
    }

    public void setHomeTeamRatioMap(Map<String, Double> homeTeamRatioMap) {
        this.homeTeamRatioMap = homeTeamRatioMap;
    }

    public Map<String, Double> getAwayTeamRatioMap() {
        return awayTeamRatioMap;
    }

    public void setAwayTeamRatioMap(Map<String, Double> awayTeamRatioMap) {
        this.awayTeamRatioMap = awayTeamRatioMap;
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

}
