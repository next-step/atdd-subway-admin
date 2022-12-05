package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import javax.persistence.*;
import java.util.ArrayList;
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
    Sections sections = new Sections();

    protected Line() {

    }

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addSection(new Section(this, upStation, downStation, distance));
    }

    public Line of (LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();

        return this;
    }

    public Sections addSection(Section section) {
        return sections.addSection(section);
    }

    public Section deleteStation(Station deleteStation) {
        return sections.deleteStation(deleteStation);
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
        return sections.getSortedSections();
    }
}
