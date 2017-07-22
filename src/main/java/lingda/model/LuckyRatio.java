package lingda.model;

/**
 * Created by lingda on 22/07/2017.
 */
public class LuckyRatio {

    private Double jufuPercent;
    private Double hgaRatio;

    @Override
    public String toString() {
        return "LuckyRatio{" +
                "jufuPercent=" + jufuPercent +
                ", hgaRatio=" + hgaRatio +
                '}';
    }

    public LuckyRatio() {
    }

    public LuckyRatio(Double jufuPercent, Double hgaRatio) {

        this.jufuPercent = jufuPercent;
        this.hgaRatio = hgaRatio;
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
