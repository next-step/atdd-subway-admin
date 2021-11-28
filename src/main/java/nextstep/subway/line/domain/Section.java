package nextstep.subway.line.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    
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

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
    
    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }

    public void checkShorter(int distance) {
        if (this.distance >= distance) {
            throw new IllegalArgumentException(String.format("길이가 맞지 않는 노선입니다.(%d)", this.distance));
        }
    }

    @Override
    public String toString() {
        return "Section [upStation=" + upStation + ", downStation=" + downStation + ", distance=" + distance + "]";
    }
    
    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }
    
    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void moveUpStationTo(Station station, int distance) {
        this.upStation = station;
        this.distance -= distance;
    }

    public void moveDownStationTo(Station station, int distance) {
        this.downStation = station;
        this.distance -= distance;
    }
}
