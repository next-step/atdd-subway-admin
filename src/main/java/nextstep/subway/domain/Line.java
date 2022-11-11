package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @Column(name = "line_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @OneToMany(mappedBy = "line")
    private final List<Station> stations = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.stations.add(upStation);
        this.stations.add(downStation);
    }

    public static Line of(final String name, final String color,
                          final Station upStation, final Station downStation) {
        return new Line(name, color, upStation, downStation);
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

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public List<Station> getStations() {
        return stations;
    }
}
