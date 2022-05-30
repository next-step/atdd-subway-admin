package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class SectionsResponse {
    private Long lineId;
    private List<SectionResponse> sections;

    private SectionsResponse() {}

    public SectionsResponse(Line line) {
        this.lineId = line.getId();
        this.sections = line.getSections()
                .stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    public Long getLineId() {
        return lineId;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
