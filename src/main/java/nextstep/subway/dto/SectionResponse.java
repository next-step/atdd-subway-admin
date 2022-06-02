package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long id;

    private Long lineId;
    private String upStationName;
    private String downStationName;
    private int distance;

    public SectionResponse() {

    }

    public static SectionResponse of (Section section) {
        return new SectionResponse(section.getId(), section.getLine().getId(), section.getUpStation().getName(), section.getDownStation().getName(), section.getDistance());
    }

    public SectionResponse(Long id, Long lineId, String upStationName, String downStationName, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getId() {
        return id;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public int getDistance() {
        return distance;
    }
}
