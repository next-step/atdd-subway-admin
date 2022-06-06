package nextstep.subway.domain;

import nextstep.subway.dto.SectionRequest;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER)
    private List<Station> stations = new LinkedList<>();

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private List<Section> sections = new LinkedList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
//        for (Station station : stations) {
//            station.setLine(this);
//        }
    }

    public void addStation(Station station) {
        this.stations.add(station);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
//        for (Section section : sections) {
//            section.setLine(this);
//        }
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void addSections(SectionRequest request) {
//        for (Section section : sections) {
//            if (section.getStation().getId().equals(request))
//        }
        System.out.println();
        sections.add(new Section(this, new Station()));
    }
}
