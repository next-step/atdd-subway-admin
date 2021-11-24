package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {

    private Long id;
    private Integer distance;

    public SectionResponse(Long id, Integer distance) {
        this.id = id;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getDistance());
    }

    public Long getId() {
        return id;
    }

    public Integer getDistance() {
        return distance;
    }
}
