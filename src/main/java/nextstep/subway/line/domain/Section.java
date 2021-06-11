package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    private static final int MINIMUN_NEW_DISTANCE = 0;
    private static final String EXCEPTION_FOR_DISTANCE = "기존 구간 안에 구간 등록시 distance는 기존 구간보다 작아야 합니다.";

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasSameUpStation(Section section) {
        return upStation.equals(section.downStation);
    }

    public boolean hasSameDownStation(Section section) {
        return downStation.equals(section.upStation);
    }

    public static Section makeAfterSection(Section preSection, Section section) {
        return new Section(preSection.line, section.downStation, preSection.downStation,
                calculateDistance(preSection, section));
    }

    public static Section makeBeforeSection(Section preSection, Section section) {
        return new Section(preSection.line, preSection.upStation, section.upStation,
                calculateDistance(preSection, section));
    }

    private static int calculateDistance(Section preSection, Section section) {
        int calculatedDistance = preSection.distance - section.distance;
        if (isIllegalDistance(calculatedDistance)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_DISTANCE);
        }
        return calculatedDistance;
    }

    private static boolean isIllegalDistance(int calculatedDistance) {
        return calculatedDistance <= MINIMUN_NEW_DISTANCE;
    }

    public boolean isEqualsUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean isEqualsDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public List<Station> findAllStations(List<Station> otherStations) {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.addAll(otherStations);
        return stations;
    }

    public List<Station> findStations(Sections sections) {
        List<Station> stations = new ArrayList<>();
        stations.add(downStation);
        Optional<Section> nextSection = sections.findSectionInUpStation(this);
        while (nextSection.isPresent()) {
            stations.add(nextSection.get().downStation);
            nextSection = sections.findSectionInUpStation(nextSection.get());
        }
        return stations;
    }

    public boolean isMatchUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isMatchDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean findNotHasDownStation(List<Section> sections) {
        return sections.stream().noneMatch(section -> section.hasSameDownStation(this));
    }

    public boolean findNotHasUpStation(List<Section> sections) {
        return sections.stream().noneMatch(section -> section.hasSameUpStation(this));
    }
}
