package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line_station.domain.LineStation;
import nextstep.subway.line_station.domain.LineStations;
import nextstep.subway.station.domain.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private final LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color, LineStation lineStation) {
        this.name = name;
        this.color = color;
        this.lineStations.add(lineStation, this);
    }

    public void update(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public List<Station> orderStationsOfLine() {
        return lineStations.orderStationsOfLine();
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

}
