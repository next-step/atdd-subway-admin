package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SectionResponses {
    private List<SectionResponse> list;

    public SectionResponses() {
        this.list = new ArrayList<>();
    }

    public SectionResponses(List<SectionResponse> list) {
        this.list = list;
    }

    public List<SectionResponse> getList() {
        return Collections.unmodifiableList(this.list);
    }

    public static SectionResponses of(List<Section> list) {
        Sections sections = new Sections(list);
        sections.sort();
        List<SectionResponse> responseList = sections.getList().stream()
                .filter(Section::isValidSection)
                .map(SectionResponse::of)
                .collect(Collectors.toList());
        return new SectionResponses(responseList);
    }
}
