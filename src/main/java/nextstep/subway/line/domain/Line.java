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

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

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

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : this.sections) {
            addStation(stations, section.getUpStation());
            addStation(stations, section.getDownStation());
        }
        return stations;
    }

    private void addStation(List<Station> stations, Station station) {
        if (!stations.contains(station)) {
            stations.add(station);
        }
    }

    public void sortBy() {
        sections.sort(Section::compareTo);
    }

}
