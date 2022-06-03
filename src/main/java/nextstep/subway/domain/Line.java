package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<LineStation> lineStations = new ArrayList<>();

    protected Line() {
    }

    private Line(Long id, String name, String color, List<LineStation> lineStations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lineStations = lineStations;
    }

    private Line(String name, String color, List<LineStation> lineStations) {
        this(null, name, color, lineStations);
    }

    public static Line of(LineRequest lineRequest, List<LineStation> lineStations) {
        return new Line(lineRequest.getName(), lineRequest.getColor(), lineStations);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
        lineStation.toLine(this);
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
        return lineStations;
    }
}
