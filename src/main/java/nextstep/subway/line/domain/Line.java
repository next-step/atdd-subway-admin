package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    private Integer distance;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Integer distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = new Sections();
    }

    public void update(Line line) {
        this.name = line.getName();

        if(Objects.nonNull(line.getColor())) {
            this.color = line.getColor();
        }

        if(Objects.nonNull(line.getDistance())) {
            this.distance = line.getDistance();
        }

        this.sections = line.getSections();
    }

    public void addSection(Station station) {
        this.sections.add(Section.create(this, station));
    }

    public void addSections(List<Station> stations) {
        this.sections.addAll(stations.stream()
                .map(s -> Section.create(this, s))
                .collect(toList())
        );
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

    public Integer getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
