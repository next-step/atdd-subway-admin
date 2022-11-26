package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @Column(nullable = false)
    private int distance;
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Long getId() {
        return id;
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

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean hasLeastOneStations(List<Station> stations) {
        return getStations().stream()
                .anyMatch(stations::contains);
    }

    public void modify(Section section) {
        modifyUpStation(section);
        modifyDownStation(section);
    }

    public boolean hasStation(Station station) {
        return getStations().contains(station);
    }

    public Line getLine() {
        return line;
    }

    public Station getStation(Station station) {
        if (station.equals(upStation)) {
            return downStation;
        }
        return upStation;
    }

    private void modifyUpStation(Section section) {
        if (!section.upStation.equals(this.upStation)) {
            return;
        }
        this.upStation = section.downStation;
        modifyDistance(section.distance);
    }

    private void modifyDownStation(Section section) {
        if (!section.downStation.equals(this.downStation)) {
            return;
        }
        this.downStation = section.upStation;
        modifyDistance(section.distance);
    }

    private void modifyDistance(int distance) {
        if (distance >= this.distance) {
            throw new IllegalArgumentException("추가할 구간의 거리가 기존 역 사이 거리보다 길 수는 없습니다.");
        }
        this.distance = this.distance - distance;
    }
}
