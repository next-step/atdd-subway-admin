package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionAddResponse {

    private Long id;
    private int distance;
    private Long upStationId;
    private Long downStationId;

    public static SectionAddResponse of(Section newDownSection) {
        return new SectionAddResponse(newDownSection);
    }

    SectionAddResponse() {

    }

    private SectionAddResponse(Section section) {
        this.id = section.getId();
        this.distance = section.getDistance();
        this.upStationId = section.getUp().getId();
        this.downStationId = section.getDown().getId();
    }

    public Long getId() {
        return this.id;
    }

    public int getDistance() {
        return this.distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

}
