package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

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

    private Line(final String name, final String color, final Section section) {
        this.name = name;
        this.color = color;
        LineStation lineStation = new LineStation(this, section);
        this.lineStations = new LineStations(lineStation);
    }

    public static Line of(final String name, final String color, final Section section) {
        return new Line(name, color, section);
    }

    public void update(Line other) {
        this.name = other.getName();
        this.color = other.getColor();
    }

    public void add(final LineStation lineStation){
        lineStations.add(lineStation);
    }

    public List<Station> getOrderedStations() {
        return lineStations.getOrderedStations();
    }
}
