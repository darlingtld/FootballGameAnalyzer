package lingda.model;

/**
 * Created by lingda on 22/07/2017.
 */
public class LuckyRatio {

    private String condition;
    private Double jufuPercent;
    private Double hgaRatio;
    private Double ratioScore;
    private Boolean isWin;

    public LuckyRatio(String condition, Double jufuPercent, Double hgaRatio, Double ratioScore, Boolean isWin) {
        this.condition = condition;
        this.jufuPercent = jufuPercent;
        this.hgaRatio = hgaRatio;
        this.ratioScore = ratioScore;

        this.isWin = isWin;
    }

    public LuckyRatio() {
    }

    @Override
    public String toString() {
        return "LuckyRatio{" +
                "condition='" + condition + '\'' +
                ", jufuPercent=" + jufuPercent +
                ", hgaRatio=" + hgaRatio +
                ", ratioScore=" + ratioScore +
                ", isWin=" + isWin +
                '}';
    }

    public Double getRatioScore() {
        return ratioScore;
    }

    public void setRatioScore(Double ratioScore) {
        this.ratioScore = ratioScore;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getWin() {

        return isWin;
    }

    public void setWin(Boolean win) {
        isWin = win;
    }

    public Double getJufuPercent() {

        return jufuPercent;
    }

    public void setJufuPercent(Double jufuPercent) {
        this.jufuPercent = jufuPercent;
    }

    public Double getHgaRatio() {
        return hgaRatio;
    }

    public void setHgaRatio(Double hgaRatio) {
        this.hgaRatio = hgaRatio;
    }
}
