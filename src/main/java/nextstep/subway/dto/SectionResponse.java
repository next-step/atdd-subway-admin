package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private long preStationId;
    private long nextStationId;
    private long distance;

    private SectionResponse(long preStationId, long nextStationId, long distance) {
        this.preStationId = preStationId;
        this.nextStationId = nextStationId;
        this.distance = distance;
    }

    public static SectionResponse from(Section section) {
        return new SectionResponse(section.getUpStationId(), section.getDownStationId(), section.getDistanceIntValue());
    }

    public long getPreStationId() {
        return preStationId;
    }

    public long getNextStationId() {
        return nextStationId;
    }

    public long getDistance() {
        return distance;
    }
}
