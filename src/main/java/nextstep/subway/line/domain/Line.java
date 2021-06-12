package nextstep.subway.line.domain;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

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

    public Line() {}

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = Sections.of(sections);
        sections.forEach(section -> section.addLine(this));
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<StationResponse> getStationResponse() {
        return Collections.unmodifiableList(sections.toResponseList());
    }

    public void addSection(Section section) {
        section.addLine(this);
        sections.add(section);
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }
}
