package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;

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
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public Line getUpdatedLineBy(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();

        return this;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section){
        this.sections.add(section);
    }
}
