package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Line() {
    }

    public Line(final Long id, final String name, final String color,
                final Station upStation, final Station downStation, final long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    private Line(final String name, final String color, final Station upStation, final Station downStation, final long distance) {
        this(null, name, color, upStation, downStation, distance);
    }

    public static Line of(final String name, final String color, final Station upStation, final Station downStation, final long distance) {
        return new Line(null, name, color, upStation, downStation, distance);
    }

    public void updateNameAndColor(final LineUpdateRequest lineUpdateRequest) {
        this.color = lineUpdateRequest.getColor();
        this.name = lineUpdateRequest.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public long getDistance() {
        return distance.getValue();
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance.getValue() +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(upStation, line.upStation) && Objects.equals(downStation, line.downStation) && Objects.equals(distance, line.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStation, downStation);
    }
}
