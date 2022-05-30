package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line extends BaseEntity {
    @Embedded
    private final LineStations lineStations = new LineStations();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(LineRequest lineRequest) {
        this(lineRequest.getName(), lineRequest.getColor());
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
        lineStations.add(lineStation);
    }

    public void update(LineRequest lineRequest) {
        name = lineRequest.getName();
        color = lineRequest.getColor();
    }
}
