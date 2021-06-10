package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        for (Section section : getSortedSections()) {
            stations.addAll(section.getStations());
        }
        return new ArrayList<>(stations);
    }

    public Section appendNewSection(Section appendSection) {
        validateExistBothUpStationAndDownStation(appendSection);
        validateNotExistBothUpStationAndDownStation(appendSection);
        if (getBeginSection().getUpStation().isEqualNameByStation(appendSection.getDownStation())
                || getEndSection().getDownStation().isEqualNameByStation(appendSection.getUpStation())) {
            this.sections.add(appendSection);
            return appendSection;
        }
        return getAppendedSectionWithUpMatchedUpAndDownMatchedDown(appendSection);
    }

    private Section getAppendedSectionWithUpMatchedUpAndDownMatchedDown(Section appendSection) {
        Map<String, Section> sectionsByUpStationName = getSectionsByUpStationName();
        Map<String, Section> sectionsByDownStationName = getSectionsByDownStationName();
        Optional<Section> sectionByMatchedUpStation = Optional.ofNullable(sectionsByUpStationName.get(appendSection.getUpStationName()));
        Optional<Section> sectionByMatchedDownStation = Optional.ofNullable(sectionsByDownStationName.get(appendSection.getDownStationName()));
        if (sectionByMatchedUpStation.isPresent() && !sectionByMatchedDownStation.isPresent()) {
            return getAppendSectionWithMatchedUpStation(appendSection, sectionsByUpStationName, sectionByMatchedUpStation.get());
        }
        return getAppendSectionWithMatchedDownStation(appendSection, sectionsByDownStationName, sectionByMatchedDownStation.get());
    }

    private Section getAppendSectionWithMatchedUpStation(Section appendSection, Map<String, Section> sectionsByUpStationName,
                                                         Section matchedSectionUpToUp) {
        Section baseSection = matchedSectionUpToUp;
        int totalDistance = baseSection.getDistance();
        String nextStationName = baseSection.getDownStationName();
        while (sectionsByUpStationName.containsKey(nextStationName) && totalDistance < appendSection.getDistance()) {
            baseSection = sectionsByUpStationName.get(nextStationName);
            totalDistance += baseSection.getDistance();
            nextStationName = baseSection.getDownStationName();
        }
        validateSameDistance(totalDistance, appendSection.getDistance());
        return getAdjustedSectionByUpStation(appendSection, baseSection, totalDistance);
    }

    private Section getAppendSectionWithMatchedDownStation(Section appendSection, Map<String, Section> sectionsByDownStationName,
                                                           Section matchedSectionDownToDown) {
        Section baseSection = matchedSectionDownToDown;
        int totalDistance = baseSection.getDistance();
        String nextStationName = baseSection.getUpStationName();
        while (sectionsByDownStationName.containsKey(nextStationName) && totalDistance < appendSection.getDistance()) {
            baseSection = sectionsByDownStationName.get(nextStationName);
            totalDistance += baseSection.getDistance();
            nextStationName = baseSection.getUpStationName();
        }
        validateSameDistance(totalDistance, appendSection.getDistance());
        return getAdjustedSectionByDownStation(appendSection, baseSection, totalDistance);
    }

    private Section getAdjustedSectionByDownStation(Section appendSection, Section baseSection, int totalDistance) {
        if (totalDistance < appendSection.getDistance()) {
            return getUpdateSectionWithLargerAppendDistance(appendSection, appendSection.getUpStation(),
                    baseSection.getUpStation(), totalDistance);
        }
        Section updateAppendSection = new Section(appendSection.getUpStation(), baseSection.getDownStation(),
                getUpdateAppendDistance(appendSection, baseSection, totalDistance));
        Section updateBaseSection = new Section(baseSection.getUpStation(), appendSection.getUpStation(),
                getUpdateBaseDistance(appendSection, totalDistance));
        return getUpdateSectionWithLargerTotalDistance(appendSection, baseSection, updateAppendSection,
                updateBaseSection);
    }

    private Section getAdjustedSectionByUpStation(Section appendSection, Section baseSection, int totalDistance) {
        if (totalDistance < appendSection.getDistance()) {
            return getUpdateSectionWithLargerAppendDistance(appendSection, baseSection.getDownStation(),
                    appendSection.getDownStation(), totalDistance);
        }
        Section updateAppendSection = new Section(baseSection.getUpStation(), appendSection.getDownStation(),
                getUpdateAppendDistance(appendSection, baseSection, totalDistance));
        Section updateBaseSection = new Section(appendSection.getDownStation(), baseSection.getDownStation(),
                getUpdateBaseDistance(appendSection, totalDistance));
        return getUpdateSectionWithLargerTotalDistance(appendSection, baseSection, updateAppendSection,
                updateBaseSection);
    }

    private int getUpdateBaseDistance(Section appendSection, int totalDistance) {
        return totalDistance - appendSection.getDistance();
    }

    private int getUpdateAppendDistance(Section appendSection, Section baseSection, int totalDistance) {
        return appendSection.getDistance() - (totalDistance - baseSection.getDistance());
    }

    private Section getUpdateSectionWithLargerTotalDistance(Section appendSection, Section baseSection,
                                                            Section updateAppendSection, Section updateBaseSection) {
        baseSection.updateSection(updateBaseSection);
        appendSection.updateSection(updateAppendSection);
        this.sections.add(appendSection);
        return appendSection;
    }

    private Section getUpdateSectionWithLargerAppendDistance(Section appendSection, Station upStation,
                                                             Station downStation, int totalDistance) {
        int updateDistance = appendSection.getDistance() - totalDistance;
        appendSection.updateSection(new Section(upStation, downStation, updateDistance));
        this.sections.add(appendSection);
        return appendSection;
    }

    private List<Section> getSortedSections() {
        Section beginSection = getBeginSection();
        Map<String, Section> sections = getSectionsByUpStationName();
        String nextStationName = beginSection.getDownStationName();
        List<Section> resultSections = new ArrayList<>(Arrays.asList(beginSection));
        while (sections.containsKey(nextStationName)) {
            nextStationName = addNextSection(resultSections, sections.get(nextStationName));
        }
        return resultSections;
    }

    private String addNextSection(List<Section> sections, Section section) {
        sections.add(section);
        return section.getDownStationName();
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

    private void validateNotExistBothUpStationAndDownStation(Section appendSection) {
        List<String> stationNames = getSortedStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        if (!stationNames.contains(appendSection.getUpStation().getName())
                && !stationNames.contains(appendSection.getDownStation().getName())) {
            throw new IllegalArgumentException("상,하행역 모두 기존 노선에 포함된 역이 아닙니다.");
        }
    }

    private void validateExistBothUpStationAndDownStation(Section appendSection) {
        List<String> stationNames = getSortedStations().stream()
                .map(Station::getName).collect(Collectors.toList());
        List<String> appendStationNames = appendSection.getStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        if (stationNames.containsAll(appendStationNames)) {
            throw new IllegalArgumentException("상행역, 하행역이 이미 존재합니다.");
        }
    }

    private void validateSameDistance(int totalDistance, int sectionDistance) {
        if (totalDistance == sectionDistance) {
            throw new IllegalArgumentException("같은 길이의 구간이 존재합니다.");
        }
    }

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
