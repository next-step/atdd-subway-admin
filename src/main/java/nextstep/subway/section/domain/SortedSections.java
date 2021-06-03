package nextstep.subway.section.domain;

import nextstep.subway.section.dto.SectionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SortedSections {
    private List<Section> sections;

    public SortedSections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        sort();
    }

    private void sort() {
        if (sections.isEmpty()) {
            return;
        }

        Section topSection = getTopSection(sections.get(0));

        this.sections = sort(topSection);
    }

    private Section getTopSection(Section firstSection) {
        for (Section section : sections) {
            if (firstSection == section) continue;

            if (firstSection.isLower(section)) {
                return getTopSection(section);
            }
        }

        return firstSection;
    }

    private List<Section> sort(Section upperSection) {
        List<Section> sortedList = new ArrayList<>();
        sortedList.add(upperSection);

        Section bottomSection = findBottomSection(upperSection);

        if (bottomSection == null) {
            return sortedList;
        }

        sortedList.addAll(sort(bottomSection));
        return sortedList;
    }

    private Section findBottomSection(Section of) {
        for (Section section : sections) {
            if (of.isUpper(section)) {
                return section;
            }
        }

        return null;
    }

    public List<SectionResponse> toResponse() {
        return sections
                .stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}
