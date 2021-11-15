package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id", cascade = CascadeType.ALL)
    private List<Section> sections;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;

        sections = new ArrayList<>();
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public boolean addSection(Section section) {
        return this.sections.add(section);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(this.sections);
    }
}
