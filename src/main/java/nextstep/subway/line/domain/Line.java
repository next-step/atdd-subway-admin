package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void update(Line line) {
        if(Objects.nonNull(line.getName())) {
            this.name = line.getName();
        }

        if(Objects.nonNull(line.getColor())) {
            this.color = line.getColor();
        }
    }

    public Long getId() {
        return id;
    }

    public Section addSection(Station upStation, Station downStation, Integer distance) {
        Section section = Section.create(this, upStation, downStation, distance);
        sections.add(section);
        return section;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public boolean removeSection(Station station) {
        return this.sections.remove(station);
    }
}
