package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private final List<Section> sections = new ArrayList<>();

    protected Line() { }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        section.toLine(this);
        sections.add(section);
    }
}
