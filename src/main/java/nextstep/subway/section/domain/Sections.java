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

    public Section addSection(Section appendSection) {
        validateExistBothUpStationAndDownStation(appendSection);
        validateNotExistBothUpStationAndDownStation(appendSection);
        if (isPlacedInFrontOrRearFor(appendSection)) {
            this.sections.add(appendSection);
            return appendSection;
        }
        return getSectionThatIsNotPlacedInFrontOrRearBy(appendSection);
    }

    private boolean isPlacedInFrontOrRearFor(Section append) {
        return getBeginSection().getUpStation().isEqualNameByStation(append.getDownStation())
                || getEndSection().getDownStation().isEqualNameByStation(append.getUpStation());
    }

    private Section getSectionThatIsNotPlacedInFrontOrRearBy(Section appendSection) {
        Distance totalDistance = new Distance();
        if (isMatchedUpStationsFor(appendSection)) {
            return getSectionThatMatchedUpStationsFor(appendSection, totalDistance);
        }
        return getSectionThatMatchedDownStationsFor(appendSection, totalDistance);
    }

    private Section getSectionThatMatchedUpStationsFor(Section appendSection, Distance totalDistance) {
        List<Section> sortedSections = getSortedSectionsFrom(appendSection.getUpStation());
        Section baseSection = getBaseSection(appendSection, totalDistance, sortedSections);
        validateSameDistance(appendSection, totalDistance);
        if (totalDistance.isGreaterThan(appendSection.getDistance())) {
            return getSectionWhenTotalDistanceIsLarge(appendSection, baseSection, totalDistance);
        }
        return getSectionWhenAppendSectionDistanceIsLarge(appendSection, baseSection, totalDistance);
    }

    private Section getSectionWhenAppendSectionDistanceIsLarge(Section appendSection, Section baseSection, Distance total) {
        Section updateAppendSection = new Section(baseSection.getDownStation(), appendSection.getDownStation(),
                appendSection.getDistance() - total.getDistance());
        this.sections.add(appendSection.updateSection(updateAppendSection));
        return appendSection;
    }

    private Section getSectionWhenTotalDistanceIsLarge(Section appendSection, Section baseSection, Distance total) {
        Section updateAppendSection = new Section(baseSection.getUpStation(), appendSection.getDownStation(),
                appendSection.getDistance() - (total.getDistance() - baseSection.getDistance()));
        Section updateBaseSection = new Section(appendSection.getDownStation(), baseSection.getDownStation(),
                total.getDistance() - appendSection.getDistance());
        baseSection.updateSection(updateBaseSection);
        this.sections.add(appendSection.updateSection(updateAppendSection));
        return appendSection;
    }

    private void validateSameDistance(Section appendSection, Distance totalDistance) {
        if (totalDistance.isEqualTo(appendSection.getDistance())) {
            throw new IllegalArgumentException("같은 길이의 구간이 존재합니다.");
        }
    }

    private Section getSectionThatMatchedDownStationsFor(Section appendSection, Distance totalDistance) {
        Section baseSection = getBaseSection(appendSection, totalDistance,
                getReverseOrderSectionsFrom(appendSection.getDownStation()));
        validateSameDistance(appendSection, totalDistance);
        if (totalDistance.isGreaterThan(appendSection.getDistance())) {
            return getSectionWhenTotalDistanceIsLargeByReverse(appendSection, baseSection, totalDistance);
        }
        return getSectionWhenAppendSectionDistanceIsLargeReverse(appendSection, baseSection, totalDistance);
    }

    private Section getSectionWhenAppendSectionDistanceIsLargeReverse(Section append, Section base, Distance total) {
        Section updateAppendSection = new Section(append.getUpStation(), base.getUpStation(),
                append.getDistance() - total.getDistance());
        this.sections.add(append.updateSection(updateAppendSection));
        return append;
    }

    private Section getSectionWhenTotalDistanceIsLargeByReverse(Section append, Section base, Distance total) {
        Section updateBaseSection = new Section(base.getUpStation(), append.getUpStation(),
                total.getDistance() - append.getDistance());
        Section updateAppendSection = new Section(append.getUpStation(), base.getDownStation(),
                append.getDistance() - (total.getDistance() - base.getDistance()));
        base.updateSection(updateBaseSection);
        this.sections.add(append.updateSection(updateAppendSection));
        return append;
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

    private Section getBaseSection(Section appendSection, Distance totalDistance, List<Section> sections) {
        return sections.stream()
                .filter(section -> totalDistance.addDistance(section.getDistance())
                        .isGreaterThanOrEqualTo(appendSection.getDistance()))
                .findAny()
                .orElse(sections.get(sections.size() - 1));
    }

    private boolean isMatchedUpStationsFor(Section append) {
        return getSectionsByUpStationName().containsKey(append.getUpStationName());
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

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
