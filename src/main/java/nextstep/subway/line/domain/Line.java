package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @OneToMany(mappedBy = "line")
    private List<Station> stations = new ArrayList<>();
    @Transient
    private final List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
        generateStations();
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

    public List<Station> getStations() {
        return this.stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    private void addSection(Section section) {
        this.sections.add(section);
    }

    public void addStation(Station station) {
        this.stations.add(station);
        station.setLine(this);
    }

    public void generateStations() {
        this.stations = new ArrayList<>();
        for (Section section : sections) {
            addStation(section.getUpStation());
            addStation(section.getDownStation());
        }
        if (sections.size() > 1) {
            addStation(sections.get(sections.size() - 1).getDownStation());
        }
    }
}
