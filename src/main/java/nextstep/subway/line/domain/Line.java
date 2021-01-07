package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

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

    @OneToMany(mappedBy = "line")
    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(String name, String color) {
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

    public List<Section> getSections() {
        return this.sections;
    }

    public List<Section> getOrderedSections() {
        List<Section> result = new ArrayList<>();
        Section nextSection = sections.stream().filter(Section::getStart)
                .findFirst()
                .get();
        while (nextSection != null) {
            result.add(nextSection);
            Station down = nextSection.getDown();
            nextSection = sections.stream()
                    .filter(item -> item.getUp() == down)
                    .findFirst().orElse(null);
        }
        return result;
    }
}
