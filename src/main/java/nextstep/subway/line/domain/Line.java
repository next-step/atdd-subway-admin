package nextstep.subway.line.domain;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Name;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Color color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = new Name(name);
        this.color = new Color(color);
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = new Name(name);
        this.color = new Color(color);
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public List<StationResponse> getStations() {
        return sections
            .stations().stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

}
