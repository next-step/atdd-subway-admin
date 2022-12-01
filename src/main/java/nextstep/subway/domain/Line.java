package nextstep.subway.domain;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String color;

    @Embedded
    private Sections sections;

    private Line() {
    }

    public Line(String name, String color) {
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

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSections(Sections sections) {
        this.sections = sections;
    }

    public void update(Line newLine) {
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }
}
