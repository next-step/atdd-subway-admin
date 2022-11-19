package nextstep.subway.domain;


import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;


@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {}

    private Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addLineStation(upStation, downStation, distance);
    }

    public void addLineStation(Station upStation, Station downStation, int distance) {
        lineStations.addLineStation(upStation, downStation, distance);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public void updateNameAndColor(String name, String color) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }

        if (StringUtils.hasText(color)) {
            this.color = color;
        }
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

    public List<Station> getLineStations() {
        return lineStations.getStations();
    }
}
