package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(final String name, final String color, final Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.sections.setLine(this);
    }

    public void update(final String name, final String color, final Station upStation, final Station downStation, final Distance distance) {
        this.name = name;
        this.color = color;
        this.sections.updateSection(upStation, downStation, distance);
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

    public Sections getSections() {
        return sections;
    }

}
