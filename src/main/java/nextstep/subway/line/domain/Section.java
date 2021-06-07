package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section implements Comparable<Section> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private Integer distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isEqualToDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isEqualToUpStation(Station station) {
        return upStation.equals(station);
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

    public Integer getDistance() {
        return distance;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public void changeDistance(Integer distance) {
        this.distance = distance;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    @Override
    public int compareTo(Section section) {

        if (isEqualToUpStation(section.getDownStation())) {
            return 1; //뒤로 간다.
        }

        // treeSet의 특성상 return 값이 0이면 add를 하지 않으므로 뒤로 가는 조건만 걸리지 않으면 무조건 앞(index==0 쪽)으로 보낸다.
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
