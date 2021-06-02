package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class SortedSections {
    private List<Section> sections;

    public SortedSections(List<Section> sections) {
        this.sections = sections;
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

    public <R1> Stream<R1> map(Function<? super Section, ? extends R1> mapper) {
        return sections.stream()
                .map(mapper);
    }
}
