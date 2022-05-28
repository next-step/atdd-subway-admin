package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void setLine(Line line) {
        this.line = line;
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

    public boolean isSameUpstation(Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean isSameUpstation(Station station) {
        return upStation.equals(station);
    }

    public boolean isSameDownStation(Section section) {
        return downStation.equals(section.downStation);
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isSameStation(Section section) {
        return isSameUpstation(section) && isSameDownStation(section);
    }

    public boolean isSameStation(Station station) {
        return isSameUpstation(station) || isSameDownStation(station);
    }

    public int diffDistance(Section section) {
        return distance - section.distance;
    }

    public int addDistance(Section section) {
        return distance + section.distance;
    }

    public void update(Section section) {
        if (isSameUpstation(section)) {
            this.upStation = section.downStation;
            this.distance = this.distance - section.distance;
        }

        if (isSameDownStation(section)) {
            this.downStation = section.upStation;
            this.distance = this.distance - section.distance;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
