package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private static final String EXCEPTION_FOR_EQUAL_SECTION = "상행역, 하행역 모두 등록되어 있습니다.";
    private static final String EXCEPTION_FOR_HAS_NOT_STATIONS = "상행역, 하행역 모두 등록되어 있지 않습니다.";

    protected Sections() {
    }

    public void add(Section section) {
        if (isEmptySection()) {
            sections.add(section);
            return;
        }

        checkExistEqualSection(section);
        checkNotExistAnyEqualStation(section);

        addEqualUpStation(section);
        addEqualDownStation(section);
        sections.add(section);
    }

    private void checkExistEqualSection(Section section) {
        if (isMatchWithUpStation(section) && isMatchWithDownStation(section)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_EQUAL_SECTION);
        }
    }

    private void checkNotExistAnyEqualStation(Section section) {
        if (!isMatchWithUpStation(section) && !isMatchWithDownStation(section)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_HAS_NOT_STATIONS);
        }
    }

    private void addEqualUpStation(Section section) {
        sections.stream()
                .filter(preSection -> preSection.isEqualsUpStation(section))
                .findFirst()
                .ifPresent(preSection -> {
                    sections.add(Section.makeAfterSection(preSection, section));
                    sections.remove(preSection);
                });
    }

    private void addEqualDownStation(Section section) {
        sections.stream()
                .filter(preSection -> preSection.isEqualsDownStation(section))
                .findFirst()
                .ifPresent(preSection -> {
                    sections.add(Section.makeBeforeSection(preSection, section));
                    sections.remove(preSection);
                });
    }

    public List<Station> findStationInSections() {
        Section firstSection = findFirstSection();
        List<Station> othersStations = firstSection.findStations(this);
        return firstSection.findAllStations(othersStations);
    }

    public Section findFirstSection() {
        return sections.stream()
                .filter(section -> section.findNotHasDownStation(sections))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Section findLastSection() {
        return sections.stream()
                .filter(section -> section.findNotHasUpStation(sections))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Optional<Section> findSectionInUpStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(newSection))
                .findFirst();
    }

    public boolean isEmptySection() {
        return sections.isEmpty();
    }

    public boolean isMatchWithUpStation(Section section) {
        return findStationInSections().stream().anyMatch(section::isMatchUpStation);
    }

    public boolean isMatchWithDownStation(Section section) {
        return findStationInSections().stream().anyMatch(section::isMatchDownStation);
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(findStationInSections());
    }

    public void removeStation(Station station) {
        ifRemoveFirstSectionUpStation(station);
        ifRemoveLastSectionDownStation(station);
       // ifRemoveInside(station);
    }

    private void ifRemoveFirstSectionUpStation(Station station) {
        if (isMatchLastSectionDownStation(station)) {
            sections.removeIf(section -> section.isMatchDownStation(station));
        }
    }

    private void ifRemoveLastSectionDownStation(Station station) {
        if (isMatchFirstSectionUpStation(station)) {
            sections.removeIf(section -> section.isMatchUpStation(station));
        }
    }

    /*private void ifRemoveInside(Station station) {
        Section afterSection = findSectionByDownStation(station);
    }*/

    /*private Section findSectionByDownStation(Station station) {
        sections.stream()
                .findFirst()
    }*/

    private boolean isMatchFirstSectionUpStation(Station station) {
        Section firstSection = findFirstSection();
        return firstSection.isMatchUpStation(station);
    }

    private boolean isMatchLastSectionDownStation(Station station) {
        Section lastSection = findLastSection();
        return lastSection.isMatchDownStation(station);
    }
}
