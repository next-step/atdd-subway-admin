package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, long distance) {
        this(name, color);
        this.sections = new Sections(
                new Section(this, null, upStation, 0),
                new Section(this, upStation, downStation, distance));
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
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

    public List<Station> getUpToDownSortedStations() {
        return sections.getStations();
    }
}
