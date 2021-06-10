package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSectionsSize(sections);
        this.sections = new ArrayList<>(sections);
    }

    public List<Section> getSections() {
        return new ArrayList<>(this.sections);
    }

    public List<Station> getSortedStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : getSortedSectionsFrom(getBeginSection().getUpStation())) {
            stations.addAll(section.getStations());
        }
        return new ArrayList<>(stations);
    }

    public Section addSection(Section newSection) {
        validateExistBothUpStationAndDownStation(newSection);
        validateNotExistBothUpStationAndDownStation(newSection);
        if (isPlacedInFrontOrRearFor(newSection)) {
            this.sections.add(newSection);
            return newSection;
        }
        return addSectionPlacedInMiddle(newSection);
    }

    private boolean isPlacedInFrontOrRearFor(Section newSection) {
        return getBeginSection().getUpStation().isEqualNameByStation(newSection.getDownStation())
                || getEndSection().getDownStation().isEqualNameByStation(newSection.getUpStation());
    }

    private Section addSectionPlacedInMiddle(Section newSection) {
        Distance totalDistance = new Distance();
        if (isIncludedInUpStations(newSection)) {
            return addSectionByMatchedUpStation(newSection, totalDistance);
        }
        return addSectionByMatchedDownStation(newSection, totalDistance);
    }

    private Section addSectionByMatchedUpStation(Section newSection, Distance totalDistance) {
        List<Section> sortedSections = getSortedSectionsFrom(newSection.getUpStation());
        Section baseSection = getBaseSection(newSection, totalDistance, sortedSections);
        validateSameDistance(newSection, totalDistance);
        if (totalDistance.isGreaterThan(newSection.getDistance())) {
            return replaceStationsByMatchedUpStationForNewAndBase(newSection, baseSection, totalDistance);
        }
        return replaceStationsByMatchedUpStationForNew(newSection, baseSection, totalDistance);
    }

    private Section replaceStationsByMatchedUpStationForNew(Section newSection, Section baseSection, Distance totalDistance) {
        Section updateAppendSection = new Section(baseSection.getDownStation(), newSection.getDownStation(),
                totalDistance.calculateDistance(newSection));
        this.sections.add(newSection.updateSection(updateAppendSection));
        return newSection;
    }

    private Section replaceStationsByMatchedUpStationForNewAndBase(Section newSection, Section baseSection,
                                                                   Distance totalDistance) {
        Section updateAppendSection = new Section(baseSection.getUpStation(), newSection.getDownStation(),
                totalDistance.calculateDistance(newSection, baseSection));
        Section updateBaseSection = new Section(newSection.getDownStation(), baseSection.getDownStation(),
                totalDistance.calculateDistance(newSection));
        baseSection.updateSection(updateBaseSection);
        this.sections.add(newSection.updateSection(updateAppendSection));
        return newSection;
    }

    private void validateSameDistance(Section newSection, Distance totalDistance) {
        if (totalDistance.isEqualTo(newSection.getDistance())) {
            throw new IllegalArgumentException("같은 길이의 구간이 존재합니다.");
        }
    }

    private Section addSectionByMatchedDownStation(Section newSection, Distance totalDistance) {
        Section baseSection = getBaseSection(newSection, totalDistance,
                getReverseOrderSectionsFrom(newSection.getDownStation()));
        validateSameDistance(newSection, totalDistance);
        if (totalDistance.isGreaterThan(newSection.getDistance())) {
            return replaceStationsByMatchedDownStationForNewAndBase(newSection, baseSection, totalDistance);
        }
        return replaceStationsByMatchedDownStationForNew(newSection, baseSection, totalDistance);
    }

    private Section replaceStationsByMatchedDownStationForNew(Section newSection, Section baseSection,
                                                              Distance totalDistance) {
        Section updateAppendSection = new Section(newSection.getUpStation(), baseSection.getUpStation(),
                totalDistance.calculateDistance(newSection));
        this.sections.add(newSection.updateSection(updateAppendSection));
        return newSection;
    }

    private Section replaceStationsByMatchedDownStationForNewAndBase(Section newSection, Section baseSection,
                                                                     Distance totalDistance) {
        Section updateBaseSection = new Section(baseSection.getUpStation(), newSection.getUpStation(),
                totalDistance.calculateDistance(newSection));
        Section updateAppendSection = new Section(newSection.getUpStation(), baseSection.getDownStation(),
                totalDistance.calculateDistance(newSection, baseSection));
        baseSection.updateSection(updateBaseSection);
        this.sections.add(newSection.updateSection(updateAppendSection));
        return newSection;
    }

    private List<Section> getSortedSectionsFrom(Station upStation) {
        Map<String, Section> sectionsByUpStationName = getSectionsByUpStationName();
        List<Section> sections = new ArrayList<>();
        String nextStationName = upStation.getName();
        while (sectionsByUpStationName.containsKey(nextStationName)) {
            sections.add(sectionsByUpStationName.get(nextStationName));
            nextStationName = sectionsByUpStationName.get(nextStationName).getDownStationName();
        }
        return sections;
    }

    private List<Section> getReverseOrderSectionsFrom(Station downStation) {
        Map<String, Section> sectionsByDownStationName = getSectionsByDownStationName();
        List<Section> sections = new ArrayList<>();
        String nextStationName = downStation.getName();
        while (sectionsByDownStationName.containsKey(nextStationName)) {
            sections.add(sectionsByDownStationName.get(nextStationName));
            nextStationName = sectionsByDownStationName.get(nextStationName).getUpStationName();
        }
        return sections;
    }

    private Section getBaseSection(Section newSection, Distance totalDistance, List<Section> sections) {
        return sections.stream()
                .filter(section -> totalDistance.addDistance(section.getDistance())
                        .isGreaterThanOrEqualTo(newSection.getDistance()))
                .findAny()
                .orElse(sections.get(sections.size() - 1));
    }

    private boolean isIncludedInUpStations(Section newSection) {
        return getSectionsByUpStationName().containsKey(newSection.getUpStationName());
    }

    private Section getBeginSection() {
        return this.sections.stream()
                .filter(section -> !getSectionsByDownStationName().containsKey(section.getUpStationName()))
                .findFirst().orElse(this.sections.stream().findFirst().get());
    }

    private Section getEndSection() {
        return this.sections.stream()
                .filter(section -> !getSectionsByUpStationName().containsKey(section.getDownStationName()))
                .findFirst().orElse(this.sections.stream().findFirst().get());
    }

    private Map<String, Section> getSectionsByUpStationName() {
        return this.sections.stream()
                .collect(Collectors.toMap(section -> section.getUpStationName(), section -> section));
    }

    private Map<String, Section> getSectionsByDownStationName() {
        return this.sections.stream()
                .collect(Collectors.toMap(section -> section.getDownStationName(), section -> section));
    }

    private void validateNotExistBothUpStationAndDownStation(Section newSection) {
        List<String> stationNames = getSortedStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        if (!stationNames.contains(newSection.getUpStation().getName())
                && !stationNames.contains(newSection.getDownStation().getName())) {
            throw new IllegalArgumentException("상,하행역 모두 기존 노선에 포함된 역이 아닙니다.");
        }
    }

    private void validateExistBothUpStationAndDownStation(Section newSection) {
        List<String> stationNames = getSortedStations().stream()
                .map(Station::getName).collect(Collectors.toList());
        List<String> appendStationNames = newSection.getStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        if (stationNames.containsAll(appendStationNames)) {
            throw new IllegalArgumentException("상행역, 하행역이 이미 존재합니다.");
        }
    }

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
