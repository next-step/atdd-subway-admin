package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
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

    public String getColor() {
        return color;
    }

    public void update(LineRequest request) {
        name = request.getName();
        color = request.getColor();
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        Station upStation = null;

        for (Section section : sections) {
            if (section.getUpStation() == null) {
                upStation = section.getDownStation();
                stations.add(upStation);
            }
        }

        List<Station> upStations = sections.stream()
                                           .map(Section::getUpStation)
                                           .filter(Objects::nonNull)
                                           .collect(Collectors.toList());

        while (upStations.contains(upStation)) {
            for (Section section : sections) {
                if (Objects.requireNonNull(upStation).equals(section.getUpStation())) {
                    upStation = section.getDownStation();
                    stations.add(section.getUpStation());
                    stations.add(section.getDownStation());
                    break;
                }
            }
        }

        return new LinkedList<>(stations);
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void addStation(Station station) {
        stations.add(station);
        if (station.getLine() != this) {
            station.setLine(this);
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        if (isAtEnd(newSection)) {
            sections.add(newSection);
            if (newSection.getLine() != this) {
                newSection.setLine(this);
            }
            return;
        }

        if (isAtBegin(newSection)) {
            sections.add(newSection);
            if (newSection.getLine() != this) {
                newSection.setLine(this);
            }
            return;
        }

        if (isAtBetween(newSection)) {
            sections.add(newSection);
            if (newSection.getLine() != this) {
                newSection.setLine(this);
            }
        }
    }

    public boolean isAtEnd(Section newSection) {
        for (Section section : sections) {
            if (section.getUpStation() != null) {
                if (section.getDownStation().equals(newSection.getUpStation())) {
                    newSection.getDownStation().setLine(this);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAtBegin(Section newSection) {
        for (Section section : sections) {
            if (section.getUpStation() == null) {
                if (section.getDownStation().equals(newSection.getDownStation())) {
                    newSection.getUpStation().setLine(this);
                    section.setUpStation(newSection.getUpStation());
                    newSection.setDownStation(newSection.getUpStation());
                    newSection.setUpStation(null);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAtBetween(Section newSection) {
        for (Section section : sections) {
            if (section.getUpStation() != null) {
                if (section.getUpStation().equals(newSection.getUpStation())) {
                    newSection.getDownStation().setLine(this);
                    newSection.setUpStation(newSection.getDownStation());
                    newSection.setDownStation(section.getDownStation());
                    section.setDownStation(newSection.getUpStation());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id)
                && Objects.equals(name, line.name)
                && Objects.equals(color, line.color)
                && Objects.equals(stations, line.stations)
                && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations, sections);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                ", sections=" + sections +
                '}';
    }
}
