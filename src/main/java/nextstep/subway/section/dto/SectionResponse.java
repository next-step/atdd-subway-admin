package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private String downStationId;
    private String upStationId;
    private int distance;

    public SectionResponse() {
    }

    private SectionResponse(String downStationId, String upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getDownStationId(), section.getUpStationId(), section.getDistance());
    }

    public String getDownStationId() {
        return downStationId;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
