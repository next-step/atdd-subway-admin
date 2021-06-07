package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {

    private final String upStationName;
    private final String downStationName;
    private final int distance;

    public SectionResponse(String upStationName, String downStationName, int distance) {
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getUpStation().getName(),
                                   section.getDownStation().getName(),
                                   section.getDistance());
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
