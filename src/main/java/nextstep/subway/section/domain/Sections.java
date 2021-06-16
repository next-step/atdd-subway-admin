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

    public List<Station> getOrderedStations() {
        List<Station> result = new LinkedList<>();
        result.add(sections.get(0).getUpStation());
        result.add(sections.get(0).getDownStation());

        addUpStation(result, result.get(0));
        addDownStation(result, result.get(result.size() - 1));

        return result;
    }

    private void addUpStation(List<Station> result, Station findStation) {
        Optional<Section> findSection = sections.stream()
                .filter(section -> section.getDownStation().equals(findStation))
                .findAny();

        if (!findSection.isPresent()) {
            return;
        }

        result.add(0, findSection.get().getUpStation());
        addUpStation(result, findSection.get().getUpStation());
    }

    private void addDownStation(List<Station> result, Station findStation) {
        Optional<Section> findSection = sections.stream()
                .filter(section -> section.getUpStation().equals(findStation))
                .findAny();

        if (!findSection.isPresent()) {
            return;
        }

        result.add(findSection.get().getDownStation());
        addDownStation(result, findSection.get().getDownStation());
    }

    private Section getSection(Station upStation, Station downStation) {
        Optional<Section> findSectionByUpStation = findSection(upStation);
        Optional<Section> findSectionByDownStation = findSection(downStation);
        checkDuplicateSectionStations(findSectionByUpStation, findSectionByDownStation);

        return findSectionByUpStation.orElseGet(findSectionByDownStation::get);
    }

    private void checkDuplicateSectionStations(Optional<Section> findSectionByUpStation, Optional<Section> findSectionByDownStation) {
        if (findSectionByUpStation.isPresent() && findSectionByDownStation.isPresent()) {
            throw new IllegalArgumentException(STATIONS_ARE_ALREADY_CONTAINS_SECTION);
        }
        if (!findSectionByUpStation.isPresent() && !findSectionByDownStation.isPresent()) {
            throw new IllegalArgumentException(NOT_FOUND_STATIONS_SECTION);
        }
    }

    private Optional<Section> findSection(Station station) {
        return sections.stream()
                .findFirst()
                .filter(section -> section.isContain(station))
                ;
    }
}
