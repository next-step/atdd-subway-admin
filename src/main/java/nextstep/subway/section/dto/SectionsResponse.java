package nextstep.subway.section.dto;

import static java.util.stream.Collectors.toList;

import java.util.List;
import nextstep.subway.section.domain.SortedSection;

public class SectionsResponse {

    private final Long lineId;
    private final List<SectionResponse> sections;

    public SectionsResponse(Long lineId, List<SectionResponse> sections) {
        this.lineId = lineId;
        this.sections = sections;
    }

    public static SectionsResponse of(Long lineId, SortedSection sortedSection) {
        return new SectionsResponse(lineId, sortedSection.getSections()
                                                         .stream()
                                                         .map(SectionResponse::of)
                                                         .collect(toList()));
    }

    public Long getLineId() {
        return lineId;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
