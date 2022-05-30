package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Line() {
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(distance, line.distance)
                && Objects.equals(id, line.id)
                && Objects.equals(name, line.name)
                && Objects.equals(color, line.color)
                && Objects.equals(upStation, line.upStation)
                && Objects.equals(downStation, line.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStation, downStation, distance);
    }
}
