package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorMessage.UP_STATION_AND_DOWN_STATION_ENROLLMENT;
import static nextstep.subway.exception.ErrorMessage.UP_STATION_AND_DOWN_STATION_NOT_FOUND;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            validateStations(section);
            ifConnectedUpStation(section);
            ifConnectedDownStation(section);
        }
        sections.add(section);
    }

    private void validateStations(Section section) {
        boolean isExistsUpStation = containUpStation(section);
        boolean isExistsDownStation = containDownStation(section);
        if (isExistsUpStation && isExistsDownStation) {
            throw new IllegalArgumentException(UP_STATION_AND_DOWN_STATION_ENROLLMENT.getMessage());
        }
        if (!isExistsUpStation && !isExistsDownStation) {
            throw new IllegalArgumentException(UP_STATION_AND_DOWN_STATION_NOT_FOUND.getMessage());
        }
    }

    private boolean containUpStation(Section section) {
        return distinctStations().contains(section.getUpStation());
    }

    private boolean containDownStation(Section section) {
        return distinctStations().contains(section.getDownStation());
    }

    private List<Station> distinctStations() {
        return sections.stream()
                .flatMap(Section::streamOfStation)
                .distinct()
                .collect(Collectors.toList());
    }

    private void ifConnectedUpStation(Section addSection) {
        for (Section section : sections) {
            if (section.getUpStation().equals(addSection.getUpStation())) {
                section.connectUpStationToDownStation(addSection);
            }
        }
    }

    private void ifConnectedDownStation(Section addSection) {
        for (Section section : sections) {
            if (section.getDownStation().equals(addSection.getDownStation())) {
                section.connectDownStationToUpStation(addSection);
            }
        }
    }

}
