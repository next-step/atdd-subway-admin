package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    protected Line(){
    }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections = Sections.from(section);
        section.setLine(this);
    }

    private Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line of(String name, String color, Sections sections){
        return new Line(name, color, sections);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, Section.of(upStation, downStation, distance));
    }

    public void update(Line newLine) {
        updateName(newLine.getName());
        updateColor(newLine.getColor());
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

    private void updateName(String name) {
        this.name = name;
    }

    private void updateColor(String color) {
        this.color = color;
    }

    public void addSection(Section section) {
        section.setLine(this);
        sections.addSection(section);
    }
}
