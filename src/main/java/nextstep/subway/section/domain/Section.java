package nextstep.subway.section.domain;

import com.sun.istack.Nullable;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    @Nullable
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section() {
    }

    public Section(int distance, Station upStation, Station station, Line line) {
        this.distance = distance;
        this.upStation = upStation;
        this.station = station;
        this.line = line;
    }

    public Section(int distance, Station upStation, Station station) {
        this.distance = distance;
        this.upStation = upStation;
        this.station = station;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getStation() {
        return station;
    }

    public Line getLine() {
        return line;
    }

    public void addLine(Line line) {
        if(Objects.nonNull(this.getLine())) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        line.getSections().add(this);
    }

    public void updateSection(Station upStation, Station station, int distance) {
        this.upStation = upStation;
        this.station = station;
        this.distance =distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) &&
                Objects.equals(station, section.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, station);
    }

    public boolean hasStation(Section newSection) {
        if (this.getStation().equals(newSection.getUpStation())
                || this.getStation().equals(newSection.getStation())) {
            return true;
        }
        return false;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
