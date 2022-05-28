package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

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

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getLineStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.addAll(section.getLineStations());
        }
        return stations;
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public void changeLineInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
