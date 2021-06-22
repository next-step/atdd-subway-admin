package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

   /* @OneToMany(mappedBy = "line")
    private List<LineStation> lineStations = new ArrayList<>();*/

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation() && oldSection.getDistance() > section.getDistance())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(section);
                    sections.add(new Section(section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance()-section.getDistance()));
                    sections.remove(oldSection);
                });
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

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        this.sections.forEach(section -> section.getStations().stream().forEach(station -> stations.add(station)));
        return stations.stream().distinct().collect(Collectors.toList());
    }
}
