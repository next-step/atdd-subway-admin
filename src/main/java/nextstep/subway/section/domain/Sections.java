package nextstep.subway.section.domain;

import static nextstep.subway.common.ErrorMessage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Section getFirstSection() {
        return sections.get(0);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void updateSection(Station upStation, Station downStation, Distance requestDistance) {
        Section section = getSection(upStation, downStation);
        section.updateStation(upStation, downStation, requestDistance);
    }

    public Section findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_SECTION))
                ;
    }

    public Section findSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_SECTION))
                ;
    }

    public boolean hasDownStation(Station findStation) {
        return sections.stream()
                .anyMatch(section -> section.isDownStation(findStation));
    }

    public boolean hasUpStation(Station findStation) {
        return sections.stream()
                .anyMatch(section -> section.isUpStation(findStation));
    }

    private Section getSection(Station upStation, Station downStation) {
        Optional<Section> findSectionByUpStation = findContainSection(upStation);
        Optional<Section> findSectionByDownStation = findContainSection(downStation);
        checkDuplicateSectionStations(findSectionByUpStation, findSectionByDownStation);

        return findSectionByUpStation.orElseGet(findSectionByDownStation::get);
    }

    private Optional<Section> findContainSection(Station station) {
        return sections.stream()
                .filter(section -> section.isContain(station))
                .findFirst()
                ;
    }

    private void checkDuplicateSectionStations(Optional<Section> findSectionByUpStation, Optional<Section> findSectionByDownStation) {
        if (findSectionByUpStation.isPresent() && findSectionByDownStation.isPresent()) {
            throw new IllegalArgumentException(STATIONS_ARE_ALREADY_CONTAINS_SECTION);
        }
        if (!findSectionByUpStation.isPresent() && !findSectionByDownStation.isPresent()) {
            throw new IllegalArgumentException(NOT_FOUND_STATIONS_SECTION);
        }
    }
}
