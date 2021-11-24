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
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
