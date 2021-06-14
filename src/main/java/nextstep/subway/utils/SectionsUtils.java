package nextstep.subway.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionsUtils {
    private static final int FIRST_INDEX = 0;
    private static final int LAST_INDEX_FIX_NUMBER = 1;
    private static final int SKIP_COUNT_ONE = 1;
    public static final int MAX_MATCH_COUNT = 2;

    public static List<Section> sortSections(List<Section> sections) {
        Section headSection = findHeadSection(sections);
        List<Section> resultSections = new ArrayList<>();
        resultSections.add(headSection);
        while (headSection.hasPostSection()) {
            headSection = headSection.getPostSection();
            resultSections.add(headSection);
        }
        return resultSections;
    }

    public static Section findHeadSection(List<Section> sections) {
        return sections.stream()
                .filter(section -> !section.hasPreSection())
                .findFirst()
                .orElse(sections.get(FIRST_INDEX));
    }

    public static Section findTailSection(List<Section> sections) {
        return sections.stream()
                .filter(section -> !section.hasPostSection())
                .findFirst()
                .orElse(sections.get(sections.size() - LAST_INDEX_FIX_NUMBER));
    }

    public static Section findTargetSectionAndAddDistance(List<Section> sections, Section newSection, Distance totalDistance) {
        validateSameDistance(newSection, totalDistance);
        Section targetSection = sections.stream().skip(SKIP_COUNT_ONE).filter(section ->
                newSection.hasDistanceShorterThanOrEqualTo(totalDistance.plusDistance(section)))
                .findAny()
                .orElse(sections.get(sections.size() - LAST_INDEX_FIX_NUMBER));
        validateSameDistance(newSection, totalDistance);
        return targetSection;
    }

    public static Section findSectionByUpStationWithThrow(List<Section> sections, Station targetStation) {
        return findSectionByUpStation(sections, targetStation)
                .orElseThrow(() -> new NoSuchElementException("구간에 포함된 역이 아닙니다."));
    }

    public static Optional<Section> findSectionByUpStation(List<Section> sections, Station targetStation) {
        return sections.stream()
                .filter(section -> section.hasSameUpStationAs(targetStation))
                .findFirst();
    }

    public static Section findSectionByDownStationWithThrow(List<Section> sections, Station targetStation) {
        return findSectionByDownStation(sections, targetStation)
                .orElseThrow(() -> new NoSuchElementException("구간에 포함된 역이 아닙니다."));
    }

    public static Optional<Section> findSectionByDownStation(List<Section> sections, Station targetStation) {
        return sections.stream()
                .filter(section -> section.hasSameDownStationAs(targetStation))
                .findFirst();
    }

    public static boolean isExistStationInSections(List<Section> sections, Section targetSection) {
        return sections.stream().anyMatch(section -> section.containStations(targetSection));
    }

    public static boolean isExistAllStationInSections(List<Section> sections, Section targetSection) {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .collect(Collectors.toSet())
                .stream()
                .filter(targetSection::containStation)
                .count() == MAX_MATCH_COUNT;

    }

    public static List<Section> makeSortedSectionsStartWith(List<Section> sections, Section newSection) {
        Section headSection = sections.stream()
                .filter(newSection::hasSameUpStationAsUpStationOf)
                .findFirst().orElse(sections.get(FIRST_INDEX));
        List<Section> resultSections = new ArrayList<>();
        resultSections.add(headSection);
        while (headSection.hasPostSection()) {
            headSection = headSection.getPostSection();
            resultSections.add(headSection);
        }
        return resultSections;
    }

    public static List<Section> makeReverseOrderSectionsStartWith(List<Section> sections, Section newSection) {
        Section headSection = sections.stream()
                .filter(newSection::hasSameDownStationAsDownStationOf)
                .findFirst().orElse(sections.get(sections.size() - LAST_INDEX_FIX_NUMBER));
        List<Section> resultSections = new ArrayList<>();
        resultSections.add(headSection);
        while (headSection.hasPreSection()) {
            headSection = headSection.getPreSection();
            resultSections.add(headSection);
        }
        return resultSections;
    }

    public static boolean existMatchedUpStation(List<Section> sections, Section sectionWithUpStation) {
        return sections.stream()
                .anyMatch(section -> section.hasSameUpStationAsUpStationOf(sectionWithUpStation));
    }

    private static void validateSameDistance(Section section, Distance targetDistance) {
        if (section.hasSameDistanceAs(targetDistance)) {
            throw new IllegalArgumentException("같은 길이의 구간이 존재합니다.");
        }
    }
}
