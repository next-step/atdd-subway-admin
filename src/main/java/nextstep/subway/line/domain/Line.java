package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionCannotAddException;
import nextstep.subway.section.domain.SectionDistanceNotEnoughException;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections;

    protected Line() {}

    public Line(Station upStation, Station downStation, int distance, String name, String color) {
        this.name = name;
        this.color = color;
        Section section = new Section(this, upStation, downStation, distance);
        this.sections = new Sections(section);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Section addSection(Station upStation, Station downStation, int distance) throws
            SectionCannotAddException,
            SectionDistanceNotEnoughException {
        Section section = new Section(this, upStation, downStation, distance);
        this.sections.addSection(section);
        return section;
    }

    public List<Station> stations() {
        return sections.orderedStations();
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

    public List<Section> sections() {
        return sections.sections();
    }
}
