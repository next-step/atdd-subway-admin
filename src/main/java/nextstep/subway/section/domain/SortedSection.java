package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import nextstep.subway.station.domain.Station;

import static java.util.stream.Collectors.toMap;

public class SortedSection {

    private static final String MESSAGE_NOT_FOUND_UPSTATION = "상행역을 찾을 수 없습니다.";

    private final List<Section> sections;

    public SortedSection(LineSections lineSections) {

        List<Section> sortedSections = lineSections.getSections();

        Section startSection = getStartSection(sortedSections);
        this.sections = Collections.unmodifiableList(linkToLastSection(startSection, sortedSections));
    }

    private Section getStartSection(List<Section> sections) {

        Map<Station, Station> stationMap =
            sections.stream()
                    .collect(toMap(Section::getDownStation,
                                   Section::getUpStation));

        Entry<Station, Station> startEntry =
            stationMap.entrySet()
                      .stream()
                      .filter(entry -> !stationMap.containsKey(entry.getValue()))
                      .findAny()
                      .orElseThrow(
                          () -> new IllegalStateException(MESSAGE_NOT_FOUND_UPSTATION));

        return sections.stream()
                       .filter(section -> section.equalsUpStation(startEntry.getValue()))
                       .filter(section -> section.equalsDownStation(startEntry.getKey()))
                       .findAny()
                       .orElseThrow(() -> new IllegalStateException(MESSAGE_NOT_FOUND_UPSTATION));
    }

    private List<Section> linkToLastSection(Section startSection, List<Section> sections) {

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
