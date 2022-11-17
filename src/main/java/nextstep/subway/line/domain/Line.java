package nextstep.subway.line.domain;

import nextstep.subway.common.vo.Color;
import nextstep.subway.common.vo.Distance;
import nextstep.subway.common.vo.Name;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {

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
        return this.sections.getStations();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name.getName();
    }

    public String getColor() {
        return this.color.getColor();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, new Distance(distance)));
    }
}
