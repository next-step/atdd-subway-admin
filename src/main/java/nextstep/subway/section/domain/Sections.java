package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;

public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int LAST_INDEX_FIX_NUMBER = 1;
    private static final int SKIP_COUNT_ONE = 1;
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

    private Section addSectionPlacedInMiddle(Section newSection) {
        if (isIncludedInUpStations(newSection)) {
            return addSectionByMatchedUpStation(newSection);
        }
        return addSectionByMatchedDownStation(newSection);
    }

    private Section addSectionByMatchedUpStation(Section newSection) {
        List<Section> sections = getSortedSectionsFrom(newSection.getUpStation());
        Distance totalDistance = Distance.copyDistanceOn(getSectionsByUpStation().get(newSection.getUpStation()));
        validateSameDistance(newSection, totalDistance);
        Section targetSection = findTargetSection(newSection, sections, totalDistance);
        if (newSection.hasLongerDistanceThan(totalDistance)) {
            return replaceStationsByMatchedUpStationForNew(newSection, targetSection, totalDistance);
        }
        return replaceStationsByMatchedUpStationForNewAndBase(newSection, targetSection, totalDistance);
    }

    private Section addSectionByMatchedDownStation(Section newSection) {
        List<Section> sections = getReverseOrderSectionsFrom(newSection.getDownStation());
        Distance totalDistance = Distance.copyDistanceOn(getSectionsByDownStation().get(newSection.getDownStation()));
        Section targetSection = findTargetSection(newSection, sections, totalDistance);
        if (newSection.hasLongerDistanceThan(totalDistance)) {
            return replaceStationsByMatchedDownStationForNew(newSection, targetSection, totalDistance);
        }
        return replaceStationsByMatchedDownStationForNewAndBase(newSection, targetSection, totalDistance);
    }

    private Section replaceStationsByMatchedUpStationForNew(Section newSection, Section baseSection, Distance totalDistance) {
        newSection.updateDistance(totalDistance);
        newSection.updateStations(new Section(baseSection.getDownStation(), newSection.getDownStation()));
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section replaceStationsByMatchedUpStationForNewAndBase(Section newSection, Section baseSection, Distance totalDistance) {
        newSection.updateDistance(baseSection, totalDistance);
        Section baseUpdateSection = new Section(newSection.getDownStation(), baseSection.getDownStation());
        Section newUpdateSection = new Section(baseSection.getUpStation(), newSection.getDownStation());
        baseSection.updateStations(baseUpdateSection);
        newSection.updateStations(newUpdateSection);
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section replaceStationsByMatchedDownStationForNew(Section newSection, Section baseSection, Distance totalDistance) {
        newSection.updateDistance(totalDistance);
        newSection.updateStations(new Section(newSection.getUpStation(), baseSection.getUpStation()));
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section replaceStationsByMatchedDownStationForNewAndBase(Section newSection, Section baseSection, Distance totalDistance) {
        newSection.updateDistance(baseSection, totalDistance);
        Section baseUpdateSection = new Section(baseSection.getUpStation(), newSection.getUpStation());
        Section newUpdateSection = new Section(newSection.getUpStation(), baseSection.getDownStation());
        baseSection.updateStations(baseUpdateSection);
        newSection.updateStations(newUpdateSection);
        addSectionWithSort(newSection);
        return newSection;
    }

    private void addSectionWithSort(Section newSection) {
        this.sections.add(newSection);
        procSortSections();
    }

    private void validateSameDistance(Section section, Distance targetDistance) {
        if (section.hasSameDistanceAs(targetDistance)) {
            throw new IllegalArgumentException("같은 길이의 구간이 존재합니다.");
        }
    }

    private Section findTargetSection(Section newSection, List<Section> sections, Distance totalDistance) {
        Section targetSection = sections.stream().skip(SKIP_COUNT_ONE).filter(section ->
                totalDistance.plusDistance(section).isGreaterThanOrEqualTo(newSection))
                .findAny()
                .orElse(sections.get(getLastIndexOf(sections)));
        validateSameDistance(newSection, totalDistance);
        return targetSection;
    }

    private Section getBeginSection() {
        return getBeginSection(this.sections);
    }

    private Section getBeginSection(List<Section> sections) {
        Map<Station, Section> sectionsByDownStation = getSectionsByDownStation(sections);
        return sections.stream()
                .filter(section -> !sectionsByDownStation.containsKey(section.getUpStation()))
                .findFirst().orElse(sections.get(FIRST_INDEX));
    }

    private Section getEndSection() {
        return this.sections.stream()
                .filter(section -> !getSectionsByUpStation().containsKey(section.getDownStation()))
                .findFirst().orElse(this.sections.get(FIRST_INDEX));
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

    private void procSortSections() {
        this.sections = getSortedSectionsFrom(getBeginSection().getUpStation());
    }

    private int getLastIndexOf(List<Section> sections) {
        return sections.size() - LAST_INDEX_FIX_NUMBER;
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
