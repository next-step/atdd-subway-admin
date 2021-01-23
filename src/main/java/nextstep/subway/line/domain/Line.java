package nextstep.subway.line.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
@Getter
@NoArgsConstructor
@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line" , cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(Section.of(this, upStation, downStation, distance));
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
    }

    public Line update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        return this;
    }

    public List<Station> getSections() {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .collect(toList());
    }
}


