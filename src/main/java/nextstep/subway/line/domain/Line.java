package nextstep.subway.line.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new LinkedList<>();

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

    public void addSection(Section section) {
        validateSection(section);

        sections.remove(section);
        sections.add(section);
    }

    private void validateSection(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("null 값을 추가할 수 없습니다.");
        }

        if (sections.contains(section)) {
            throw new IllegalArgumentException("이미 추가 된 Section 입니다.");
        }
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
