package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Color color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(long id, Name name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(Name name, Color color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        return this.sections.findStations();
    }

    public Long getId() {
        return this.id;
    }

    public Name getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public int getDistance() {
        return this.sections.findDistance();
    }

    public void addSection(Station upStation, Station downStation, Distance distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Name name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void removeSection(Station station) {
        this.sections.removeStation(station);
    }
}
