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

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
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

    public void replaceUpStation(Section section) {
        this.upStation = section.getDownStation();
        distance.minus(section.distance);
    }
    public void replaceDownStation(Section section) {
        this.downStation = section.getUpStation();
        distance.minus(section.distance);
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
