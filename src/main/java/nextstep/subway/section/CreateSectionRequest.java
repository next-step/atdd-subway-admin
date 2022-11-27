package nextstep.subway.section;

public class CreateSectionRequest {

    private long upStationId;
    private long downStationId;
    private long distance;

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}