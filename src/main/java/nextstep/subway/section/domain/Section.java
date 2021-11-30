package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public boolean hasDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public boolean hasUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public void updateDown(Section added) {
        this.downStation = added.getUpStation();
        this.distance = this.distance.subtract(added.getDistance());
    }

    public void updateUp(Section added) {
        this.upStation = added.getDownStation();
        this.distance = this.distance.subtract(added.getDistance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section)o;
        return Objects.equals(id, section.id) &&
            Objects.equals(upStation, section.upStation) &&
            Objects.equals(downStation, section.downStation) &&
            Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Section{");
        sb.append("id=").append(id);
        sb.append(", down=").append(downStation);
        sb.append(", up=").append(upStation);
        sb.append(", distance=").append(distance);
        sb.append('}');
        return sb.toString();
    }
}
