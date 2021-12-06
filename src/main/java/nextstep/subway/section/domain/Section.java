package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Line.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne(targetEntity = Station.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne(targetEntity = Station.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() {
    }

    public Section(final Line line, final Station upStation, final Station downStation, final Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(final Station upStation, final Station downStation, final Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public boolean connectable(final Section section) {
        if (this.equals(section)) {
            return false;
        }

        long matchCount = Arrays.asList(upStation, downStation)
                .stream()
                .filter(station -> !(station.equals(section.getUpStation()) && station.equals(section.getDownStation()))
                        && (station.equals(section.getUpStation()) || station.equals(section.getDownStation())))
                .count();

        return matchCount == 1;
    }

    public void deductDistance(final Section section) {
        if (this.upStation.equals(section.getUpStation())) {
            this.upStation = section.getDownStation();
            this.distance = this.distance.deduct(section.getDistance());
        }

        if (this.downStation.equals(section.getDownStation())) {
            this.downStation = section.getUpStation();
            this.distance = this.distance.deduct(section.getDistance());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Section) || o == null || getClass() != o.getClass()) {
            return false;
        }

        Section that = (Section) o;
        if (this.line.equals(that.getLine()) &&
            this.upStation.equals(that.getUpStation()) &&
            this.downStation.equals(that.getDownStation()) &&
            this.distance.equals(that.getDistance())) {
            return true;
        }

        return false;
    }

}
