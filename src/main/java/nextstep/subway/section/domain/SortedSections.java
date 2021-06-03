package nextstep.subway.section.domain;

import nextstep.subway.section.dto.SectionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SortedSections {
    private List<Section> sortedSections;

    public SortedSections(List<Section> sections) {
        this.sortedSections = sort(sections);
    }

    private List<Section> sort(List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>();
        List<Section> copiedSection = new ArrayList<>(sections);

        TopSection topSection = new TopSection(copiedSection);

        while (topSection.getTopSection() != null) {
            Section section = topSection.getTopSection();

            sortedSections.add(section);
            copiedSection.remove(section);

            topSection = new TopSection(copiedSection);
        }

        return sortedSections;
    }

    public List<SectionResponse> toResponse() {
        return sortedSections
                .stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}
