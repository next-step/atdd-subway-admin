package nextstep.subway.domain;

import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @NotNull
    private String color;
    @OneToMany(mappedBy = "line")
    private List<Station> stations = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addStation(Station station) {
        station.toLine(this);
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

    public List<Station> getStations() {
        return stations;
    }

    public void modifyBy(LineUpdateRequest request) {
        this.name = request.getName();
        this.color = request.getColor();
    }

    public void resetStations() {
        new ArrayList<>(stations).forEach(Station::resetLine);
    }
}
