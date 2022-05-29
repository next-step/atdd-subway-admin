package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    private Line(LineName name, LineColor color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(LineName name, LineColor color) {
        return new Line(name, color);
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }


    public List<StationResponse> getAllStations() {
        return findAllStations().stream().
                map(StationResponse::of).
                collect(Collectors.toList());
    }

    private Set<Station> findAllStations() {
        return sections.stream().
                flatMap(s -> s.getBothStations().stream()).
                collect(Collectors.toSet());
    }

    public void update(String name, String color) {
        this.name = LineName.from(name);
        this.color = LineColor.from(color);
    }

    public void addSection(Section section) {
        //TODO: Section 검증 로직 추가
        this.sections.add(section);
        section.setLine(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
                && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
