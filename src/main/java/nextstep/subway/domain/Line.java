package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line(String name) {
        this(name, null);
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    protected Line() {

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

    public List<Station> getLineStations() {
        return sections.getSections().stream()
                .flatMap(s -> s.getLineStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        sections.addSection(section);
        section.setLine(this);
    }

    public void changeLineInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
