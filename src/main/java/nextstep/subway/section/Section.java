package nextstep.subway.section;

public class Section {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
