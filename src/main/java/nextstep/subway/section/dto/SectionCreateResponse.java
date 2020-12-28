package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionCreateResponse {

    private Long id;

    public SectionCreateResponse(Long id) {
        this.id = id;
    }

    public static SectionCreateResponse of(Section section) {
        return new SectionCreateResponse(section.getId());
    }
}
