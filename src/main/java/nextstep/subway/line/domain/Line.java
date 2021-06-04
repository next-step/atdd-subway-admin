package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Line() { }

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
//        for (int i = 0; i < sections.size(); ++i) {
//            sections.get(i).setLine(this);
//        }
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
        section.setLine(this);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.sections = line.getSections();
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < sections.size(); ++i) {
            stations.add(sections.get(i).getUpStation());
            stations.add(sections.get(i).getDownStation());
        }
        return stations;
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

    public void addSection(Section section) {
        if (!this.sections.contains(section)) {
            this.sections.add(section);
        }
    }
}
