package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    public static String DUPLICATE_SECTION_ERROR = "중복된 지하철 노선을 등록할 수 없습니다.";
    public static String NOT_MATCH_STATION_ERROR = "상행역과 하행역 둘 중 하나도 포함되지 않은 경우 지하철 노선을 등록할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSections(Section section) {
        validate(section);
        checkUpStation(section);
        checkDownStation(section);
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        Set<Station> stations = new HashSet<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return new ArrayList<>(stations);
    }

    private void validate(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(DUPLICATE_SECTION_ERROR);
        }

        if (getStations().size() > 0 && !isContainsStation(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(NOT_MATCH_STATION_ERROR);
        }
    }

    private boolean isContainsStation(Station upStation, Station downStation) {
        return getStations().contains(upStation) || getStations().contains(downStation);
    }

    private void checkUpStation(Section section) {
        Station upStation = section.getUpStation();

        if (matchUpStation(upStation)) {
            Section existingUpStation = existingUpStation(upStation);
            existingUpStation.updateUpStation(section.getDownStation(), section.getDistance());
        }
    }

    private void checkDownStation(Section section) {
        Station downStation = section.getDownStation();

        if (matchDownStation(downStation)) {
            Section existingUpStation = existingDownStation(downStation);
            existingUpStation.updateDownStation(section.getUpStation());
        }
    }

    private boolean matchUpStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsUpStation(station));
    }

    private Section existingUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행역을 찾을 수 없습니다."));
    }

    private boolean matchDownStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsDownStation(station));
    }

    private Section existingDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("하행역을 찾을 수 없습니다."));
    }
}
