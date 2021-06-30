package nextstep.subway.section.domain;

import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(optional = false)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {

    }

    public Section(Long id, Line line, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Distance getDistance() {
        return this.distance;
    }

    public boolean isIncludeStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public void connectSectionBetween(Section section) {
        replaceUpStationIfSameUpStation(section);
        replaceDownStationIfSameDownStation(section);
        distance.minus(section.distance);
    }

    private void replaceUpStationIfSameUpStation(Section section) {
        if(this.upStation.equals(section.getUpStation())) {
            this.upStation = section.getDownStation();
        }
    }

    private void replaceDownStationIfSameDownStation(Section section) {
        if(this.downStation.equals(section.getDownStation())) {
            this.downStation = section.getUpStation();
        }
    }

    public boolean isSameStationWithUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameStationWithDownStation(Station station) {
        return this.downStation.equals(station);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
