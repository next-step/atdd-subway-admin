package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String color;

    @Embedded
    private final LineStations lineStations = LineStations.empty();

    protected Line() {
    }

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }
    
    public static Line of(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addLineStation(LineStation newLineStation) {
        newLineStation.toLine(this);
        lineStations.add(newLineStation);
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

    public List<LineStation> getLineStations() {
        return lineStations.get();
    }
}
