package nextstep.subway.domain;

import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(nullable = false)
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
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

    public List<Station> stations() {
        return sections.stations();
    }

    public void modifyBy(LineUpdateRequest request) {
        this.name = request.getName();
        this.color = request.getColor();
    }

    public void addSection(Station upStation, Station downStation, long distance) {
        sections.add(new Section(this, upStation, downStation, distance));
    }
}
