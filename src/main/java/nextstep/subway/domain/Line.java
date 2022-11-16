package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Long getId() {
        return id;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        section.toLine(this);
        sections.add(section);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Map<String, Object>> getStations() {
        List<Map<String, Object>> stations = sections.stream()
                .map(section -> section.getUpStation().toMapForOpen())
                .collect(Collectors.toList());
        Section lastSection = sections.get(sections.size() - 1);
        stations.add(lastSection.getDownStation().toMapForOpen());
        return stations;
    }

    public void update(Line updateLine) {
        this.name = updateLine.name;
        this.color = updateLine.color;
    }
}
