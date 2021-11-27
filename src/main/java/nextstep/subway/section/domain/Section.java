package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station up;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station down;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station up, Station down, Distance distance) {
        this.up = up;
        this.down = down;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getDown() {
        return down;
    }

    public Station getUp() {
        return up;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Section))
            return false;
        Section section = (Section)o;
        return distance == section.distance &&
            Objects.equals(id, section.id) &&
            Objects.equals(up, section.up) &&
            Objects.equals(down, section.down);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, up, down, distance);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Section{");
        sb.append("id=").append(id);
        sb.append(", down=").append(down);
        sb.append(", up=").append(up);
        sb.append(", distance=").append(distance);
        sb.append('}');
        return sb.toString();
    }
}
