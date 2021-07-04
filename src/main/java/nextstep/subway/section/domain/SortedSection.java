package nextstep.subway.section.domain;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import nextstep.subway.station.domain.Station;

public class SortedSection {

    private final List<Section> sections;

    public SortedSection(LineSections lineSections) {
        Set<Section> sortedSections = lineSections.getSections();
        Section startSection = lineSections.getStartSection();

        this.sections = Collections
            .unmodifiableList(linkToLastSection(startSection, sortedSections));
    }

    private List<Section> linkToLastSection(Section startSection, Set<Section> sections) {

        Map<Station, Section> sectionMap =
            sections.stream()
                .collect(toMap(Section::getUpStation,
                    Function.identity()));

        List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(startSection);

        Section nextSection = sectionMap.get(startSection.getDownStation());
        while (nextSection != null) {
            sortedSections.add(nextSection);
            nextSection = sectionMap.get(nextSection.getDownStation());
        }

        return sortedSections;
    }

    public List<Section> getSections() {
        return sections;
    }
}
