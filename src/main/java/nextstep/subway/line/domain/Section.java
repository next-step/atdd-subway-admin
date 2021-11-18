package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"up_station_id", "down_station_id"}))
@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }
    
    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section valueOf(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public void addSectionAtLine(Line line) {
        this.line = line;
        this.line.addSection(this);
    }

    public Long getId() {
        return this.id;
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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }
}
