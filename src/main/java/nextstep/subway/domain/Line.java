package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {}

    public Line(String name, String color, Long distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;

        addSection(upStation, downStation, distance);
    }

    public Line modify(LineRequest.Modification modify) {
        this.name = modify.getName();
        this.color = modify.getColor();

        return this;
    }

    public void addSection(Station upStation, Station downStation, Long distance) {
        lineStations.add(upStation, downStation, distance);
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

    public List<Station> getStationOrderedUpToDown() {
        return this.lineStations.getStationsSortedByUpToDown();
    }
}
