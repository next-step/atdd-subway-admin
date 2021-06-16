package nextstep.subway.section.domain;

import static nextstep.subway.common.ErrorMessage.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Station> getStations() {
        List<Station> result = new LinkedList<>();
        result.add(sections.get(0).getUpStation());
        result.add(sections.get(0).getDownStation());

        addUpStation(result, result.get(0));
        addDownStation(result, result.get(result.size() - 1));

        return result;
    }

    public void updateSection(Station upStation, Station downStation, Distance requestDistance) {
        Section section = getSection(upStation, downStation);
        section.updateStation(upStation, downStation, requestDistance);
    }

    private void addUpStation(List<Station> result, Station findStation) {
        while (findDownStation(findStation)) {
            Station copyStation = findStation;
            Section findSection = sections.stream()
                    .filter(section -> section.getDownStation().equals(copyStation))
                    .findAny().orElseThrow(() -> new RuntimeException(NOT_FOUND_STATION));

            result.add(0, findSection.getUpStation());
            findStation = findSection.getUpStation();
        }
    }

    private void addDownStation(List<Station> result, Station findStation) {
        while (findUpStation(findStation)) {
            Station copyStation = findStation;
            Section findSection = sections.stream()
                    .filter(section -> section.getUpStation().equals(copyStation))
                    .findAny().orElseThrow(() -> new RuntimeException(NOT_FOUND_STATION));

            result.add(findSection.getDownStation());
            findStation = findSection.getDownStation();
        }
    }

    private boolean findDownStation(Station findStation) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(findStation));
    }

    private boolean findUpStation(Station findStation) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(findStation));
    }

    private Section getSection(Station upStation, Station downStation) {
        checkDuplicateSectionStations(upStation, downStation);

        return sections.stream()
                .findFirst()
                .filter(section -> section.isContain(upStation) || section.isContain(downStation))
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATIONS_SECTION));
    }

    private void checkDuplicateSectionStations(Station upStation, Station downStation) {
        if (isContain(upStation) && isContain(downStation)) {
            throw new IllegalArgumentException(STATIONS_ARE_ALREADY_CONTAINS_SECTION);
        }
    }

    private boolean isContain(Station station) {
        return sections.stream()
                .findFirst()
                .filter(section -> section.isContain(station))
                .isPresent();
    }
}
