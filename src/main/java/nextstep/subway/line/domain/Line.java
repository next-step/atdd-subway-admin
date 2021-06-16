package nextstep.subway.line.domain;

import java.util.List;

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

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(LineRequest request, Station upStation, Station downStation) {
        this.name = request.getName();
        this.color = request.getColor();
        addSection(Section.getInstance(this, upStation, downStation, request.getDistance()));
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

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void addSection(Station upStation, Station downStation, Distance requestDistance) {
        sections.updateSection(upStation, downStation, requestDistance);
        sections.addSection(Section.getInstance(this, upStation, downStation, requestDistance));
    }

    public List<Station> stations() {
        return sections.getStations();
    }

    public void updateNameColor(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
