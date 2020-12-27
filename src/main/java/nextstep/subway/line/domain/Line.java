package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line")
@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private LineStations lineStations;

    private Line(final String name, final String color, final LineStations lineStations) {
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
        this.lineStations = Objects.requireNonNull(lineStations);
    }

    public static Line of(final String name, final String color) {
        LineStations lineStations = new LineStations();
        return new Line(name, color, lineStations);
    }

    // todo: 종점역(상행, 하행)에 대한 수정
    public void update(Line other) {
        this.name = other.getName();
        this.color = other.getColor();
    }

    public void add(final Section section) {
        lineStations.add(new LineStation(this, section));
    }

    public List<Station> getStationsOrderByUp() {
        return lineStations.getStationsOrderByUp();
    }
}
