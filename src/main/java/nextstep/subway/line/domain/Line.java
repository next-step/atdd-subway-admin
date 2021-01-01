package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.distance.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
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

    private String color;

    @Embedded
    private final Sections sections = Sections.newInstance();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(final String name, final String color, final Station upStation, final Station downStation, final long distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upStation, downStation, new Distance(distance)));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(final Section newSection) {
        sections.add(newSection);
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
        return sections.getStations();
    }
}
