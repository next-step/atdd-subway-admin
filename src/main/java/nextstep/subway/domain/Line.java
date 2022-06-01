package nextstep.subway.domain;

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

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        Section section = new Section(upStation, downStation, distance);
        section.setLine(this);
        this.sections = Sections.from(Arrays.asList(section));
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
}
