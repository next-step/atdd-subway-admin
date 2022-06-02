package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    public static final String HAS_UP_AND_DOWN_STATION_MSG = "상행역과 하행역이 이미 다른 구간에 동시에 등록되어 있습니다.";
    public static final String HAS_NOT_UP_AND_DOWN_STATION_MSG = "새로운 구간에 상행역과 하행역 중 하나는 포함되어야 합니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStationsSorted() {
        List<Station> stations = new ArrayList<>();

        Station rootStation = getRootStation();
        stations.add(rootStation);
        Station nextStation = getNextStation(rootStation);

        while(Objects.nonNull(nextStation)) {
            stations.add(nextStation);
            nextStation = getNextStation(nextStation);
        }

        return stations;
    }

    private Station getNextStation(Station downStation) {
        Section nextSection = sections.stream()
                .filter(section -> section.getEqualsUpStation(downStation))
                .findFirst()
                .orElse(null);

        if(Objects.nonNull(nextSection))
            return nextSection.getDownStation();

        return null;
    }

    public void addSection(Section section) {
        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSectionValid(section);
        updateExistSection(section);
        sections.add(section);
    }

    private void addSectionValid(Section section) {
        if(hasUpStationAndDownStation(section)) {
            throw new IllegalArgumentException(HAS_UP_AND_DOWN_STATION_MSG);
        }

        if(hasNotUpStationAndDownStation(section)) {
            throw new IllegalArgumentException(HAS_NOT_UP_AND_DOWN_STATION_MSG);
        }
    }

    private boolean hasUpStationAndDownStation(Section newSection) {
        return getStations().contains(newSection.getUpStation()) &&
                getStations().contains(newSection.getDownStation());
    }

    private boolean hasNotUpStationAndDownStation(Section newSection) {
        return !getStations().contains(newSection.getUpStation()) &&
                !getStations().contains(newSection.getDownStation());
    }
    
    private void updateExistSection(Section newSection) {
        //역과 역사이 (상행이 같을 때)
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpSection(newSection));

        //역과 역사이 (하행이 같을 때)
        sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownSection(newSection));
    }

    public Set<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    private Station getRootStation() {
        return getUpStations().stream()
                .filter(station -> !getDownStations().contains(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Set<Station> getUpStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation()))
                .collect(Collectors.toSet());
    }

    private Set<Station> getDownStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getDownStation()))
                .collect(Collectors.toSet());
    }

    public List<Section> getSections() {
        return sections;
    }
}
