package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private String upStation;
    private String downStation;
    private int distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                section.getUpStation().getName(), section.getDownStation().getName(), section.getDistance());
    }

    private SectionResponse() {
    }

    private SectionResponse(String upStation, String downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public String getUpStation() {
        return upStation;
    }

    public String getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
