package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;
import nextstep.subway.utils.DistanceCalculator;

public class Sections {
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        validateSectionsSize(sections);
        initSectionsWithSort(sections);
    }

    public List<Section> getSections() {
        return new ArrayList<>(this.sections);
    }

    public List<Station> getSortedStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : this.sections) {
            stations.addAll(section.getStations());
        }
        return new ArrayList<>(stations);
    }

    public Section addSection(Section newSection) {
        validateExistBothUpStationAndDownStation(newSection);
        validateNotExistBothUpStationAndDownStation(newSection);
        if (isPlacedInFrontOrRearFor(newSection)) {
            addSectionWithSort(newSection);
            return newSection;
        }
        return addSectionPlacedInMiddle(newSection);
    }

    private boolean isPlacedInFrontOrRearFor(Section newSection) {
        return getBeginSection().getUpStation().equals(newSection.getDownStation())
                || getEndSection().getDownStation().equals(newSection.getUpStation());
    }

    private void initSectionsWithSort(List<Section> newSections) {
        Map<Station, Section> sectionsByUpStation = getSectionsByUpStation(newSections);
        Station nextStation = getBeginSection(newSections).getUpStation();
        while (sectionsByUpStation.containsKey(nextStation)) {
            this.sections.add(sectionsByUpStation.get(nextStation));
            nextStation = sectionsByUpStation.get(nextStation).getDownStation();
        }
    }

    private void addSectionWithSort(Section newSection) {
        this.sections.add(newSection);
        procSortSections();
    }

    private Section addSectionPlacedInMiddle(Section newSection) {
        DistanceCalculator totalDistanceCalculator = new DistanceCalculator();
        if (isIncludedInUpStations(newSection)) {
            return addSectionByMatchedUpStation(newSection, totalDistanceCalculator);
        }
        return addSectionByMatchedDownStation(newSection, totalDistanceCalculator);
    }

    private Section addSectionByMatchedUpStation(Section newSection, DistanceCalculator totalDistanceCalculator) {
        List<Section> sortedSections = getSortedSectionsFrom(newSection.getUpStation());
        Section baseSection = getBaseSection(newSection, totalDistanceCalculator, sortedSections);
        validateSameDistance(newSection, totalDistanceCalculator);
        if (totalDistanceCalculator.isGreaterThan(newSection.getDistance())) {
            return replaceStationsByMatchedUpStationForNewAndBase(newSection, baseSection, totalDistanceCalculator);
        }
        return replaceStationsByMatchedUpStationForNew(newSection, baseSection, totalDistanceCalculator);
    }

    private Section replaceStationsByMatchedUpStationForNew(Section newSection, Section baseSection, DistanceCalculator totalDistanceCalculator) {
        Section updateAppendSection = new Section(baseSection.getDownStation(), newSection.getDownStation(),
                totalDistanceCalculator.calculateDistance(newSection));
        addSectionWithSort(newSection, updateAppendSection);
        return newSection;
    }

    private Section replaceStationsByMatchedUpStationForNewAndBase(Section newSection, Section baseSection,
                                                                   DistanceCalculator totalDistanceCalculator) {
        Section updateAppendSection = new Section(baseSection.getUpStation(), newSection.getDownStation(),
                totalDistanceCalculator.calculateDistance(newSection, baseSection));
        Section updateBaseSection = new Section(newSection.getDownStation(), baseSection.getDownStation(),
                totalDistanceCalculator.calculateDistance(newSection));
        baseSection.updateSection(updateBaseSection);
        addSectionWithSort(newSection, updateAppendSection);
        return newSection;
    }

    private void validateSameDistance(Section newSection, DistanceCalculator totalDistanceCalculator) {
        if (totalDistanceCalculator.isEqualTo(newSection.getDistance())) {
            throw new IllegalArgumentException("같은 길이의 구간이 존재합니다.");
        }
    }

    private Section addSectionByMatchedDownStation(Section newSection, DistanceCalculator totalDistanceCalculator) {
        Section baseSection = getBaseSection(newSection, totalDistanceCalculator,
                getReverseOrderSectionsFrom(newSection.getDownStation()));
        validateSameDistance(newSection, totalDistanceCalculator);
        if (totalDistanceCalculator.isGreaterThan(newSection.getDistance())) {
            return replaceStationsByMatchedDownStationForNewAndBase(newSection, baseSection, totalDistanceCalculator);
        }
        return replaceStationsByMatchedDownStationForNew(newSection, baseSection, totalDistanceCalculator);
    }

    private Section replaceStationsByMatchedDownStationForNew(Section newSection, Section baseSection,
                                                              DistanceCalculator totalDistanceCalculator) {
        Section updateAppendSection = new Section(newSection.getUpStation(), baseSection.getUpStation(),
                totalDistanceCalculator.calculateDistance(newSection));
        addSectionWithSort(newSection, updateAppendSection);
        return newSection;
    }

    private Section replaceStationsByMatchedDownStationForNewAndBase(Section newSection, Section baseSection,
                                                                     DistanceCalculator totalDistanceCalculator) {
        Section updateBaseSection = new Section(baseSection.getUpStation(), newSection.getUpStation(),
                totalDistanceCalculator.calculateDistance(newSection));
        Section updateAppendSection = new Section(newSection.getUpStation(), baseSection.getDownStation(),
                totalDistanceCalculator.calculateDistance(newSection, baseSection));
        baseSection.updateSection(updateBaseSection);
        addSectionWithSort(newSection, updateAppendSection);
        return newSection;
    }

    private Section getBaseSection(Section newSection, DistanceCalculator totalDistanceCalculator, List<Section> sections) {
        return sections.stream()
                .filter(section -> totalDistanceCalculator.addDistance(section.getDistance())
                        .isGreaterThanOrEqualTo(newSection.getDistance()))
                .findAny()
                .orElse(sections.get(sections.size() - 1));
    }

    private Section getBeginSection() {
        return getBeginSection(this.sections);
    }

    private Section getBeginSection(List<Section> sections) {
        Map<Station, Section> sectionsByDownStation = getSectionsByDownStation(sections);
        return sections.stream()
                .filter(section -> !sectionsByDownStation.containsKey(section.getUpStation()))
                .findFirst().orElse(sections.stream().findFirst().get());
    }

    private Section getEndSection() {
        return this.sections.stream()
                .filter(section -> !getSectionsByUpStation().containsKey(section.getDownStation()))
                .findFirst().orElse(this.sections.stream().findFirst().get());
    }

    private List<Section> getSortedSectionsFrom(Station upStation) {
        Map<Station, Section> sectionsByUpStationName = getSectionsByUpStation();
        List<Section> sections = new ArrayList<>();
        Station nextStation = upStation;
        while (sectionsByUpStationName.containsKey(nextStation)) {
            sections.add(sectionsByUpStationName.get(nextStation));
            nextStation = sectionsByUpStationName.get(nextStation).getDownStation();
        }
        return sections;
    }

    private List<Section> getReverseOrderSectionsFrom(Station downStation) {
        Map<Station, Section> sectionsByDownStationName = getSectionsByDownStation();
        List<Section> sections = new ArrayList<>();
        Station nextStation = downStation;
        while (sectionsByDownStationName.containsKey(nextStation)) {
            sections.add(sectionsByDownStationName.get(nextStation));
            nextStation = sectionsByDownStationName.get(nextStation).getUpStation();
        }
        return sections;
    }

    private Map<Station, Section> getSectionsByUpStation() {
        return getSectionsByUpStation(this.sections);
    }

    private Map<Station, Section> getSectionsByUpStation(List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toMap(section -> section.getUpStation(), section -> section));
    }

    private Map<Station, Section> getSectionsByDownStation() {
        return getSectionsByDownStation(this.sections);
    }

    private Map<Station, Section> getSectionsByDownStation(List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toMap(section -> section.getDownStation(), section -> section));
    }

    private boolean isIncludedInUpStations(Section newSection) {
        return getSectionsByUpStation().containsKey(newSection.getUpStation());
    }

    private void addSectionWithSort(Section newSection, Section updateAppendSection) {
        addSectionWithSort(newSection.updateSection(updateAppendSection));
    }

    private void procSortSections() {
        this.sections = getSortedSectionsFrom(getBeginSection().getUpStation());
    }

    private void validateNotExistBothUpStationAndDownStation(Section newSection) {
        if (!getSortedStations().contains(newSection.getUpStation())
                && !getSortedStations().contains(newSection.getDownStation())) {
            throw new IllegalArgumentException("상,하행역 모두 기존 노선에 포함된 역이 아닙니다.");
        }
    }

    private void validateExistBothUpStationAndDownStation(Section newSection) {
        if (getSortedStations().containsAll(newSection.getStations())) {
            throw new IllegalArgumentException("상행역, 하행역이 이미 존재합니다.");
        }
    }

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
