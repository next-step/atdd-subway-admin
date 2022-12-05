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

    protected Line() {}

    private Line( String name, String color, int distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(new Section(upStation, downStation, distance, this));
    }

    public static Line of(String name ,String color, int distance, Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void update(Line newLine) {
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
        section.setLine(this);
    }

    private void validateSection(Section section) {
        if (section.isExistSections(sections.getStations())) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 등록되어 있습니다.");
        }
        if (!section.isIncludeStation(sections.getStations())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.");
        }
    }
}
