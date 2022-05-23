package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = requireNonNull(name, "이름이 비었습니다");
        this.color = requireNonNull(color, "색상이 비었습니다");
        addSection(new Section(upStation, downStation, distance));
    }

    protected Line() {
    }

    private void addSection(Section section) {
        sections.add(section);
        section.bindLine(this);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void update(String name, String color) {
        this.name = requireNonNull(name, "이름이 비었습니다");
        this.color = requireNonNull(color, "색상이 비었습니다");
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
}
