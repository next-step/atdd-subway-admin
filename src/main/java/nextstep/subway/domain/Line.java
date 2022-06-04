package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String color;

    @Embedded
    Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Integer distance) {
        this.name = name;
        this.color = color;
        addSection(new Section(upStation, downStation, distance, this));
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void change(String name, String color) {
        this.name = requireNonNull(name, "name 값을 입력해주세요.");
        this.color = requireNonNull(color, "color 값을 입력해주세요.");
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
}
