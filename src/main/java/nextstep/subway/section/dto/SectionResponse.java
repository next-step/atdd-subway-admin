package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {

    private Long id;
    private String upStation;
    private String downStation;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long id, String upStation, String downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation().getName(), section.getDownStation().getName(), section.getDistance());
    }

    public Long getId() {
        return id;
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
