package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void changeLine(Line line) {
        if (Objects.nonNull(this.line)) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        line.getSections().add(this);
    }

    public void updateDistance(int distance) {
        this.distance = distance;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public boolean containsAndSamePosition(Section section) {
        return containsSomeStation(section)
            && containsExactlyStationPosition(section);
    }

    public boolean containsAndNotSamePosition(Section section) {
        return containsSomeStation(section)
            && !containsExactlyStationPosition(section);
    }

    boolean containsStation(Long stationId) {
        List<Long> stationIds = Arrays.asList(upStation.getId(), downStation.getId());
        return stationIds.contains(stationId);
    }

    private boolean containsSomeStation(Section section) {
        List<Station> stations = Arrays.asList(upStation, downStation);
        return stations.contains(section.getUpStation())
            || stations.contains(section.getDownStation());
    }

    private boolean containsExactlyStationPosition(Section section) {
        return upStation.equals(section.upStation)
            || downStation.equals(section.downStation);
    }
}
