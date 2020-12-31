package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

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

    public Sections getSections() {
        return this.sections;
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        if(section.getLine() != this) {
            section.addLine(this);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Optional<Section> firstSection = sections.findSectionWithUpStation(null);

        while(firstSection.isPresent()) {
            Station station = firstSection.get().getStation();
            stations.add(station);
            firstSection = sections.findSectionWithUpStation(station);
        }
        return stations;
    }

    public void removeSection(Section section) {
        this.sections.getSections().remove(section);
    }
}
