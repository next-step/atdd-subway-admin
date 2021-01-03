package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @ManyToMany
    @JoinTable(name = "station_line"
            , joinColumns = @JoinColumn(name = "line_id")
            , inverseJoinColumns = @JoinColumn(name = "station_id"))
    @JsonManagedReference
    private List<Station> stations = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = line.getStations();
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

    private void addStations(Station station) {
        this.stations.add(station);
//        station.addLine(this);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSections(Station upwardStation, Station downStation, int distance) {
        this.addSections(new Section(this, upwardStation, downStation, distance));
    }

    public void addSections(Section section) {
        this.sections.add(section);
        this.addStations(section.getUpStation());
        this.addStations(section.getDownStation());
        section.setLine(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) &&
                Objects.equals(name, line.name) &&
                Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
