package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this(null, name, color, section);
    }

    public Line(Long id, String name, String color, Section section) {
        this.id = id;
        this.name = name;
        this.color = color;
        addSection(section);
    }

    public void update(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public void addSection(Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public void updateSection(Section section) {
        section.setLine(this);
        sections.update(section);
    }

    public void removeSectionByStation(Station station) {
        sections.delete(station);
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

    public List<Station> orderedStations() {
        return sections.orderedStations();
    }

}
