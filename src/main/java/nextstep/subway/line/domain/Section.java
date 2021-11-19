package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    private int orders;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public void updateUpStation(Station upStation, int distance) {
        this.upStation = upStation;
        this.distance = distance;
    }

    public void updateDownStation(Station downStation, int distance) {
        this.downStation = downStation;
        this.distance = distance;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line.getId() +
                ", upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(upStation.getId(), section.upStation.getId())
                && Objects.equals(downStation.getId(), section.downStation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance, orders);
    }
}

