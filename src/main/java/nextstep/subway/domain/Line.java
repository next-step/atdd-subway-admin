package nextstep.subway.domain;

import nextstep.subway.exception.NotFoundSectionException;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        addSection(new Section(this, upStation, downStation, new Distance(distance)));
    }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line of(String name, String color, Long distance, Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public static Line of(String name, String color, Sections sections) {
        return new Line(name, color, sections);
    }

    public void addSection(Section newSection) {
        this.sections.add(newSection);
    }

    public void update(Line newLine) {
        this.name = newLine.name;
        this.color = newLine.color;
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

    public void removeSection(Station station) throws NotFoundSectionException {
        this.sections.remove(station);
    }
}
