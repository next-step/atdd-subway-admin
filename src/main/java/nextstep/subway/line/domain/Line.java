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

    @Column(name = "distance")
    private int distance;

    @Embedded
    private LastStation lastStation;

    @Embedded
    private LineStations lineStations;

    private Line(final String name, final String color, final int distance, final LastStation lastStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.lastStation = lastStation;
    }

    public static Line of(final String name, final String color, final int distance, final LastStation lastStation) {
        Line line = new Line(name, color, distance, lastStation);
        line.initLineStation();
        return line;
    }

    private void initLineStation() {
        LineStation lineStation = LineStation.of(this, lastStation.getUpStation(), lastStation.getDownStation(), distance);
        lineStations = LineStations.init(lineStation);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.distance = line.distance;
        this.lastStation = line.lastStation;
    }

    public void addLineStation(final Station upStation, final Station downStation, final int distance) {
        LineStation lineStation = LineStation.of(this, upStation, downStation, distance);
        lineStations.add(lineStation);
    }

    public List<Station> getStations() {
        return lineStations.getStationsOrderByUp(lastStation.getUpStation());
    }
}
