package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_station_id")
    private Station previousStation;

    @ManyToOne
    @JoinColumn(name = "now_station_id", nullable = false)
    private Station nowStation;

    @Column
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station nowStation) {
        this(line, null, nowStation, null);
    }

    public Section(Line line, Station previousStation, Station nowStation, Long distance) {
        this.line = line;
        this.previousStation = previousStation;
        this.nowStation = nowStation;
        this.distance = distance;
    }

    public Station previousStation() {
        return previousStation;
    }

    public Station nowStation() {
        return nowStation;
    }

    public Long distanceFromPreviousStation() {
        return distance;
    }

    public boolean isLineFirstSection() {
        return this.previousStation == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line)
                && Objects.equals(previousStation, section.previousStation) && Objects.equals(
                nowStation, section.nowStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, previousStation, nowStation, distance);
    }
}
