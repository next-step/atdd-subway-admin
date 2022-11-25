package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    protected Line() {}

    public Line(String name, String color) {
        validation(name, color);
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        validation(name, color);
        this.name = name;
        this.color = color;
        addSection(section);
    }

    private void validation(String name, String color) {
        if (name == null || name.isEmpty()) {
            new IllegalArgumentException(ErrorCode.NO_EMPTY_LINE_NAME_EXCEPTION.getErrorMessage());
        }
        if (color == null || color.isEmpty()) {
            new IllegalArgumentException(ErrorCode.NO_EMPTY_LINE_COLOR_EXCEPTION.getErrorMessage());
        }
    }

    private void addSection(Section section) {
        this.sections.add(section);
    }

    public Long findId() {
        return this.id;
    }

    public String findName() {
        return this.name;
    }

    public String findColor() {
        return this.color;
    }

    public void modify(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
