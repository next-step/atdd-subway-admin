package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    public static final String SECTION_DUPLICATE_EXCEPTION_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.";
    public static final String SECTION_CONTAINS_EXCEPTION_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.";
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (size() >= 1) {
            validateSection(section);
            validateContainsSection(section);
        }
        this.sections.add(section);
    }

    private void validateContainsSection(Section section) {
        if (!getStations().contains(section.getUpStation()) && !getStations().contains(section.getDownStation())) {
            throw new IllegalArgumentException(SECTION_CONTAINS_EXCEPTION_MESSAGE);
        }
    }

    private void validateSection(Section section) {
        if (getStations().contains(section.getUpStation()) && getStations().contains(section.getDownStation())) {
            throw new IllegalArgumentException(SECTION_DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public int size() {
        return this.sections.size();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        addFirstSectionStations(stations);
        addStations(stations);
        return stations;
    }

    private void addStations(List<Station> stations) {
        Station upStation = findDownStationByUpStation(findFirstUpStation());
        while (!isLastDownStation(upStation)) {
            addStation(stations, upStation);
            upStation = findDownStationByUpStation(upStation);
        }
    }

    private void addFirstSectionStations(List<Station> stations) {
        Station firstUpStation = findFirstUpStation();
        stations.add(firstUpStation);
        stations.add(findDownStationByUpStation(firstUpStation));
    }

    private Station findFirstUpStation() {
        Station firstUpStation = this.sections.get(0).getUpStation();
        for (Section section : this.sections) {
            firstUpStation = findUpStation(firstUpStation, section);
        }
        return firstUpStation;
    }

    private Station findDownStationByUpStation(Station upStation) {
        Station downStation = upStation;
        for (Section section : this.sections) {
            downStation = findDownStationByUpStation(upStation, downStation, section);
        }
        return downStation;
    }

    private boolean isLastDownStation(Station upStation) {
        return this.sections.stream()
                .noneMatch(section -> section.isUpStation(upStation));
    }

    private static Station findDownStationByUpStation(Station upStation, Station downStation, Section section) {
        Station station = downStation;
        if (section.isUpStation(upStation)) {
            station = section.getDownStation();
        }
        return station;
    }

    private void addStation(List<Station> stations, Station upStation) {
        this.sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .map(Section::getDownStation)
                .forEach(stations::add);
    }

    private static Station findUpStation(Station firstUpStation, Section section) {
        Station upStation = firstUpStation;
        if (section.isDownStation(firstUpStation)) {
            upStation = section.getUpStation();
        }
        return upStation;
    }
}
