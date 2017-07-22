package lingda.model;

import java.util.List;

/**
 * Created by lingda on 22/07/2017.
 */
public class Bingo {

    private String homeTeam;
    private String awayTeam;
    private String league;

    @Override
    public String toString() {
        return "Bingo{" +
                "homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", league='" + league + '\'' +
                ", luckyRatioList=" + luckyRatioList +
                '}';
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
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

    public List<LuckyRatio> getLuckyRatioList() {
        return luckyRatioList;
    }

    public void setLuckyRatioList(List<LuckyRatio> luckyRatioList) {
        this.luckyRatioList = luckyRatioList;
    }

    private List<LuckyRatio> luckyRatioList;
}
