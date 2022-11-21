package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class SectionsResponse {
    private List<SectionResponse> sections;

    public SectionsResponse(List<SectionResponse> sections) {
        this.sections = sections;
    }

    public static SectionsResponse of(Line line) {
        List<SectionResponse> sections = line.getSections().allSortedSections().stream()
                .map(SectionResponse::new).collect(Collectors.toList());
        return new SectionsResponse(sections);
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
