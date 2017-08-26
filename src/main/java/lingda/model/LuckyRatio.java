package lingda.model;

/**
 * Created by lingda on 22/07/2017.
 */
public class LuckyRatio {

    private String condition;
    private Double fanbodanPercent;
    private Double bodanRatio;
    private Double ratioScore;
    private Boolean isWin;

    public LuckyRatio(String condition, Double fanbodanPercent, Double bodanRatio, Double ratioScore, Boolean isWin) {
        this.condition = condition;
        this.fanbodanPercent = fanbodanPercent;
        this.bodanRatio = bodanRatio;
        this.ratioScore = ratioScore;

        this.isWin = isWin;
    }

    public LuckyRatio() {
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Double getFanbodanPercent() {
        return fanbodanPercent;
    }

    public void setFanbodanPercent(Double fanbodanPercent) {
        this.fanbodanPercent = fanbodanPercent;
    }

    public Double getBodanRatio() {
        return bodanRatio;
    }

    public void setBodanRatio(Double bodanRatio) {
        this.bodanRatio = bodanRatio;
    }

    public Double getRatioScore() {
        return ratioScore;
    }

    public void setRatioScore(Double ratioScore) {
        this.ratioScore = ratioScore;
    }

    public Boolean getWin() {
        return isWin;
    }

    public void setWin(Boolean win) {
        isWin = win;
    }
}
