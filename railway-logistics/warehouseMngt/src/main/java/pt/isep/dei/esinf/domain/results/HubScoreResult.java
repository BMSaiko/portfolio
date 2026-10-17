package pt.isep.dei.esinf.domain.results;

public class HubScoreResult implements Comparable<HubScoreResult> {
    private final String stid;
    private final String stname;
    private final int degree;
    private final double betweenness;
    private final double harmonicCloseness;
    private final double hubScore;

    public HubScoreResult(String stid, String stname, int degree, double betweenness, double harmonicCloseness, double hubScore) {
        this.stid = stid;
        this.stname = stname;
        this.degree = degree;
        this.betweenness = betweenness;
        this.harmonicCloseness = harmonicCloseness;
        this.hubScore = hubScore;
    }

    // Getters
    public String getStid() {
        return stid;
    }

    public String getStname() {
        return stname;
    }

    public int getDegree() {
        return degree;
    }

    public double getBetweenness() {
        return betweenness;
    }

    public double getHarmonicCloseness() {
        return harmonicCloseness;
    }

    public double getHubScore() {
        return hubScore;
    }

    @Override
    public int compareTo(HubScoreResult other) {
        // Descending order by hubScore
        return Double.compare(other.hubScore, this.hubScore);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%d,%.6f,%.6f,%.6f", stid, stname, degree, betweenness, harmonicCloseness, hubScore);
    }
}