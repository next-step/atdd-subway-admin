package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorMessage.UP_STATION_AND_DOWN_STATION_ENROLLMENT;
import static nextstep.subway.exception.ErrorMessage.UP_STATION_AND_DOWN_STATION_NOT_FOUND;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            // TODO: 구간 중복 또는 길이 관련 유효성 처리
            validateStations(section);
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

}
