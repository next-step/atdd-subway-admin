package nextstep.subway.section.domain;

import nextstep.subway.common.exception.SectionDoesNotDeleteStationException;
import nextstep.subway.common.exception.SectionNotContainsStationException;
import nextstep.subway.section.domain.spcification.SectionsAddableSpecifications;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final int MINIMUM_SECTION_SIZE = 1;
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections.addAll(sections);
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.addAll(
                    section.getStations()
            );
        }

        return stations;
    }

    public void addSectionBy(Section section) {
        connectNewStationToNearStationsAndResize(section);

        sections.add(section);
    }

    public boolean isAddable(Station upStation, Station downStation, Distance distance) {
        if (sections.isEmpty()) {
            return true;
        }

        return new SectionsAddableSpecifications(sections, upStation, downStation, distance)
                .isAddable();
    }

    public SortedSections getSortedSections() {
        return new SortedSections(this.sections);
    }

    public Section deleteSectionBy(Station station) {
        if (sections.size() <= MINIMUM_SECTION_SIZE) {
            throw new SectionDoesNotDeleteStationException("최소 1개 이상의 구간을 가지고 있어야 합니다.");
        }

        Section deletableSectionByStation = findDeletableSectionBy(station)
                .orElseThrow(() -> new SectionNotContainsStationException("삭제할 수 있는 구간이 없습니다."));

        deleteSection(deletableSectionByStation);

        connectNearStationToNearStationAndResize(deletableSectionByStation, station);

        return deletableSectionByStation;
    }

    private void connectNewStationToNearStationsAndResize(Section section) {
        sections.stream()
                .filter(item -> item != section)
                .filter(item -> item.isSameUpStation(section) || item.isSameDownStation(section))
                .findFirst()
                .ifPresent(near -> near.connectNewStationToNearStationsAndResize(section));
    }

    private void connectNearStationToNearStationAndResize(Section section, Station station) {
        sections.stream()
                .filter(item -> item != section)
                .filter(item -> item.isUpStation(station) || item.isDownStation(station))
                .findFirst()
                .ifPresent(near -> near.connectNearStationToNearStationAndResize(section));

    }

    private Optional<Section> findDeletableSectionBy(Station station) {
        Section section = sections.stream()
                .filter(item -> item.isUpStation(station))
                .findFirst()
                .orElse(findBottomSectionBy(station));

        return Optional.ofNullable(section);
    }

    private Section findBottomSectionBy(Station station) {
        return sections.stream()
                .filter(item -> item.isDownStation(station))
                .findFirst()
                .orElse(null);
    }

    private void deleteSection(Section section) {
        sections.remove(section);
        section.removeLine();
    }
}
