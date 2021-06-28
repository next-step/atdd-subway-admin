package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    public Line() {
    }

    public Line(String name, String color, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.lineStations = lineStations;
    }

    public Line(String name, String color){
        this.name = name;
        this.color = color;
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

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Long> getStationIds(){
        return this.lineStations.getStationIds();
    }

    public void addLineStation(LineStation lineStation){
        this.lineStations.add(lineStation);
    }

    public Optional<LineStation> getSameLineStation(long id){
        return this.lineStations.getSameLineStation(id);
    }

    public List<LineStation> getLineStationOrdered(){
        return this.lineStations.getStationsOrdered();
    }
}
