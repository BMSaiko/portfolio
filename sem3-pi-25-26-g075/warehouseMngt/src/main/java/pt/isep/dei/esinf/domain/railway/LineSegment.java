package pt.isep.dei.esinf.domain.railway;


public class LineSegment {
    private int id;
    private int lineId;
    private Facility startFacility;
    private Facility endFacility;
    private boolean isElectrified;
    private int length; // meters
    private int numberOfTracks;
    private int speedLimitKmh;
    private Integer sidingId; // nullable

    public LineSegment(int id, int lineId, Facility startFacility, Facility endFacility,
                       boolean isElectrified, int length, int numberOfTracks,
                       int speedLimitKmh, Integer sidingId) {
        this.id = id;
        this.lineId = lineId;
        this.startFacility = startFacility;
        this.endFacility = endFacility;
        this.isElectrified = isElectrified;
        this.length = length;
        this.numberOfTracks = numberOfTracks;
        this.speedLimitKmh = speedLimitKmh;
        this.sidingId = sidingId;
    }

    public int getId() { return id; }
    public Facility getStartFacility() { return startFacility; }
    public Facility getEndFacility() { return endFacility; }
    public boolean isElectrified() { return isElectrified; }
    public int getLength() { return length; }
    public int getNumberOfTracks() { return numberOfTracks; }
    public int getSpeedLimitKmh() { return speedLimitKmh; }
    public boolean isSingleTrack() { return numberOfTracks == 1; }
    public boolean hasSiding() { return sidingId != null; }
}
