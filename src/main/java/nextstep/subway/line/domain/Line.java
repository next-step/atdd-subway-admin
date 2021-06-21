package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
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
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(LineRequest request, Station upStation, Station downStation) {
        this.name = request.getName();
        this.color = request.getColor();
        addSection(upStation, downStation, request.getDistance());
    }

    public Section createSection(Station upStation, Station downStation, Distance requestDistance) {
        sections.updateSection(upStation, downStation, requestDistance);
        return addSection(upStation, downStation, requestDistance);
    }

    public Sections sections() {
        return sections;
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public void updateNameColor(String name, String color) {
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

    private Section addSection(Station upStation, Station downStation, Distance requestDistance) {
        Section section = Section.getInstance(this, upStation, downStation, requestDistance);
        sections.addSection(section);
        return section;
    }
}
