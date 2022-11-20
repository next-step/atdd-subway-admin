package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

public class Sections {
    private static final String ERROR_MESSAGE_DUPLICATE_UP_DOWN_STATION = "동일한 상행/하행선을 등록할 수 없습니다.";
    private static final String ERROR_MESSAGE_NONE_MATCH_UP_DOWN_STATION = "상행/하행선은 하나라도 입력되어야 합니다.";
    private static final String ERROR_MESSAGE_NOT_NULL_UP_STATION = "상행종점은 필수입니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section addSection) {
        validDuplicateSection(addSection);
        validNotContainSection(addSection);

        Optional<Section> upSection = findUpSection(addSection);
        Optional<Section> downSection = findDownSection(addSection);

        upSection.ifPresent(section -> section.updateUpStation(addSection));
        downSection.ifPresent(section -> section.updateDownStation(addSection));
        sections.add(addSection);
    }

    private void validDuplicateSection(Section compareSection) {
        if (isContainsAllStation(compareSection)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_UP_DOWN_STATION);
        }
    }

    private boolean isContainsAllStation(Section compareSection) {
        return sections.stream().map(Section::stations)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .containsAll(compareSection.stations());
    }

    private void validNotContainSection(Section compareSection) {
        if (sections.isEmpty()) {
            return;
        }

        if (getStations().stream().noneMatch(station -> compareSection.stations().contains(station))) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NONE_MATCH_UP_DOWN_STATION);
        }
    }

    private Optional<Section> findUpSection(Section addSection) {
        return sections.stream().filter(addSection::isSameUpStationBySection).findFirst();
    }

    private Optional<Section> findDownSection(Section addSection) {
        return sections.stream().filter(addSection::isSameDownStationBySection).findFirst();
    }

    public void removeStation(Station station) {
        Optional<Section> sectionContainUpStation = findSectionContainUpStation(station);
        Optional<Section> sectionContainDownStation = findSectionContainDownStation(station);

        sectionContainUpStation.ifPresent(section -> sections.remove(section));
        sectionContainDownStation.ifPresent(section -> sections.remove(section));
    }

    private Optional<Section> findSectionContainUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStationByStation(station))
                .findFirst();
    }

    private Optional<Section> findSectionContainDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStationByStation(station))
                .findFirst();
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Station> getSortStation() {
        Map<Station, Station> stationMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        return sortStations(findTopStation(), stationMap);
    }

    private List<Station> sortStations(Station topStation, Map<Station, Station> stationMap) {
        List<Station> stations = new LinkedList<>();
        stations.add(topStation);
        Station upStation = topStation;
        while (stationMap.get(upStation) != null) {
            upStation = stationMap.get(upStation);
            stations.add(upStation);
        }
        return stations;
    }

    private Station findTopStation() {
        Set<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());

        return sections.stream().map(Section::getUpStation)
                .filter(station -> !downStations.contains(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_NULL_UP_STATION));
    }

    public int totalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }
}
