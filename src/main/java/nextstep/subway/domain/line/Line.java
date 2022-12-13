package nextstep.subway.domain.line;

import nextstep.subway.domain.BaseEntity;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.request.LineRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.lineStations.addLineStations(lineStations);
    }

    public void updateLine(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getName();
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

    public LineStations getLineStations() {
        return lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        this.lineStations.addLineStation(lineStation);
    }

    public void removeStation(Station stationToDelete) {
        lineStations.removeStation(stationToDelete, this);
    }
}
