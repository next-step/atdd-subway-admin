package nextstep.subway.section.domain;

import static nextstep.subway.common.ErrorMessage.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    public static final int SECTIONS_DELETE_FOR_MIN_COUNT = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void updateSection(Station upStation, Station downStation, Distance requestDistance) {
        Section section = getSection(upStation, downStation);
        section.updateStation(upStation, downStation, requestDistance);
    }

    public List<Station> toStationResponse() {
        List<Station> result = new LinkedList<>();

        result.add(getFirstSection().getUpStation());
        result.add(getFirstSection().getDownStation());

        addUpStation(result);
        addDownStation(result);

        return result;
    }

    public void removeSection(Station station) {
        boolean hasUpStation = hasUpStation(station);
        boolean hasDownStation = hasDownStation(station);

        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException(NOT_FOUND_SECTION);
        }

        if (sections.size() <= SECTIONS_DELETE_FOR_MIN_COUNT) {
            throw new RuntimeException(SECTIONS_HAVE_NOT_MIN_COUNT);
        }

        if (hasUpStation && hasDownStation) {
            Section sectionByDownStation = findSectionByDownStation(station);
            Section sectionByUpStation = findSectionByUpStation(station);
            sectionByDownStation.updateSectionByDownStation(sectionByUpStation);
            sections.remove(sectionByUpStation);
            return;
        }
        if (hasUpStation) {
            Section sectionByUpStation = findSectionByUpStation(station);
            sections.remove(sectionByUpStation);
        }
        if (hasDownStation) {
            Section sectionByDownStation = findSectionByDownStation(station);
            sections.remove(sectionByDownStation);
        }
    }

    private Section getFirstSection() {
        return sections.get(0);
    }

    private Section findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_SECTION))
                ;
    }

    private Section findSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(NOT_FOUND_SECTION))
                ;
    }

    private void addUpStation(List<Station> result) {
        Station findStation = result.get(0);
        while (hasDownStation(findStation)) {
            Section findSection = findSectionByDownStation(findStation);
            result.add(0, findSection.getUpStation());
            findStation = findSection.getUpStation();
        }
    }

    private void addDownStation(List<Station> result) {
        Station findStation = result.get(result.size() - 1);
        while (hasUpStation(findStation)) {
            Section findSection = findSectionByUpStation(findStation);
            result.add(findSection.getDownStation());
            findStation = findSection.getDownStation();
        }
    }

    private boolean hasDownStation(Station findStation) {
        return sections.stream()
                .anyMatch(section -> section.isDownStation(findStation));
    }

    private boolean hasUpStation(Station findStation) {
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
