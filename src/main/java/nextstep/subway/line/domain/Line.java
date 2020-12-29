package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color, Station front, Station back, int distance) {
        this(name, color);
        Section section = new Section(this, front, back, distance);
        this.sections.add(section);
    }

    public Line(String name, String color) {
        // TODO : private access
        this.name = name;
        this.color = color;
        this.sections = new ArrayList<>();
    }

    public void update(Line line) {
        // TODO : parameter split
        this.name = line.getName();
        this.color = line.getColor();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }
}
