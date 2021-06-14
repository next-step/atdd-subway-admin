package nextstep.subway.section.domain;

import static nextstep.subway.utils.SectionsUtils.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import nextstep.subway.station.domain.Station;

public class Sections {
    private static final int NON_DELETABLE_SIZE = 1;
    private List<Section> sections;

    public Sections(List<Section> sections) {
        validateSectionsSize(sections);
        this.sections = sortSections(sections);
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
            connectLinkOfSections(newSection);
            addSectionWithSort(newSection);
            return newSection;
        }
        return addSectionPlacedInMiddle(newSection);
    }

    public Section removeSectionByStation(Station targetStation) {
        Optional<Section> findSection = findSectionByUpStation(this.sections, targetStation);
        if (findSection.isPresent()) {
            return removeSectionByMatchedUpStation(findSection.get());
        }
        return removeSectionByMatchedDownStation(targetStation);
    }

    private void connectLinkOfSections(Section newSection) {
        Section headSection = findHeadSection(this.sections);
        if (headSection.hasSameUpStationAsDownStationOf(newSection)) {
            headSection.connectPreSection(newSection);
            return;
        }
        Section tailSection = findTailSection(this.sections);
        tailSection.connectPostSection(newSection);
    }

    private Section addSectionPlacedInMiddle(Section newSection) {
        if (existMatchedUpStation(this.sections, newSection)) {
            return addSectionByMatchedUpStation(newSection);
        }
        return addSectionByMatchedDownStation(newSection);
    }

    private Section addSectionByMatchedUpStation(Section newSection) {
        Distance totalDistance = Distance.copyOn(findSectionByUpStationWithThrow(this.sections, newSection.getUpStation()));
        Section targetSection = findTargetSectionAndAddDistance(makeSortedSectionsStartWith(this.sections, newSection),
                newSection, totalDistance);
        if (newSection.hasLongerDistanceThan(totalDistance)) {
            return replaceStationsByMatchedUpStationForNew(newSection, targetSection, totalDistance);
        }
        return replaceStationsByMatchedUpStationForNewAndBase(newSection, targetSection, totalDistance);
    }

    private Section addSectionByMatchedDownStation(Section newSection) {
        Distance totalDistance = Distance.copyOn(findSectionByDownStationWithThrow(this.sections, newSection.getDownStation()));
        Section targetSection = findTargetSectionAndAddDistance(makeReverseOrderSectionsStartWith(this.sections, newSection),
                newSection, totalDistance);
        if (newSection.hasLongerDistanceThan(totalDistance)) {
            return replaceStationsByMatchedDownStationForNew(newSection, targetSection, totalDistance);
        }
        return replaceStationsByMatchedDownStationForNewAndBase(newSection, targetSection, totalDistance);
    }

    private Section replaceStationsByMatchedUpStationForNew(Section newSection, Section baseSection, Distance totalDistance) {
        newSection.updateStations(new Section(baseSection.getDownStation(), newSection.getDownStation()));
        newSection.connectPreSection(baseSection);
        newSection.updateDistance(totalDistance);
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section replaceStationsByMatchedUpStationForNewAndBase(Section newSection, Section baseSection, Distance totalDistance) {
        Section baseUpdateSection = new Section(newSection.getDownStation(), baseSection.getDownStation());
        Section newUpdateSection = new Section(baseSection.getUpStation(), newSection.getDownStation());
        baseSection.updateStations(baseUpdateSection);
        newSection.updateStations(newUpdateSection);
        newSection.interceptPreSection(baseSection);
        newSection.updateDistance(baseSection, totalDistance);
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section replaceStationsByMatchedDownStationForNew(Section newSection, Section baseSection, Distance totalDistance) {
        newSection.updateStations(new Section(newSection.getUpStation(), baseSection.getUpStation()));
        newSection.connectPostSection(baseSection);
        newSection.updateDistance(totalDistance);
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section replaceStationsByMatchedDownStationForNewAndBase(Section newSection, Section baseSection, Distance totalDistance) {
        Section baseUpdateSection = new Section(baseSection.getUpStation(), newSection.getUpStation());
        Section newUpdateSection = new Section(newSection.getUpStation(), baseSection.getDownStation());
        baseSection.updateStations(baseUpdateSection);
        newSection.updateStations(newUpdateSection);
        newSection.interceptPostSection(baseSection);
        newSection.updateDistance(baseSection, totalDistance);
        addSectionWithSort(newSection);
        return newSection;
    }

    private Section removeSectionByMatchedUpStation(Section section) {
        validateLastSection();
        section.updatePreSectionInfo();
        section.connectPreToPost();
        this.sections.remove(section);
        return section;
    }

    private Section removeSectionByMatchedDownStation(Station targetStation) {
        Section findSection = findSectionByDownStationWithThrow(this.sections, targetStation);
        validateLastSection();
        findSection.connectPreToPost();
        this.sections.remove(findSection);
        return findSection;
    }

    private void validateLastSection() {
        if (this.sections.size() == NON_DELETABLE_SIZE) {
            throw new IllegalArgumentException("마지막 구간에 포함된 역은 삭제할 수 없습니다.");
        }
    }

    private void addSectionWithSort(Section newSection) {
        this.sections.add(newSection);
        this.sections = sortSections(this.sections);
    }

    private boolean isPlacedInFrontOrRearFor(Section newSection) {
        return findHeadSection(this.sections).hasSameUpStationAsDownStationOf(newSection)
                || findTailSection(this.sections).hasSameDownStationAsUpStationOf(newSection);
    }

    private void validateNotExistBothUpStationAndDownStation(Section newSection) {
        if (!isExistStationInSections(this.sections, newSection)) {
            throw new IllegalArgumentException("상,하행역 모두 기존 노선에 포함된 역이 아닙니다.");
        }
    }

    private void validateExistBothUpStationAndDownStation(Section newSection) {
        if (isExistAllStationInSections(this.sections, newSection)) {
            throw new IllegalArgumentException("상행역, 하행역이 이미 존재합니다.");
        }
    }

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
