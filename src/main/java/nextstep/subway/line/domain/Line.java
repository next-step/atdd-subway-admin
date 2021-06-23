package nextstep.subway.line.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private final LineStations stations = new LineStations();

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private final List<Section> sections = new LinkedList<>();

    protected Line() {}

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

    public Section addStations(Station upStation, Station downStation, int distance) throws
            LineStationDuplicatedException {
        Section section = new Section(this, upStation, downStation, distance);
        this.stations.add(this, upStation);
        this.stations.add(this, downStation);
        this.sections.add(section);
        return section;
    }

    public List<Station> stations() {
        return sections.stream()
            .map(Section::stations)
            .reduce(new LinkedList<>(), Line::serializeStations);
    }

    private static List<Station> serializeStations(List<Station> a, List<Station> b) {
        if (a.size() == 0) {
            // 항상 첫번째 argument를 반환하기 위함.
            a.addAll(b);
            return a;
        }
        a.add(b.get(1));
        return a;
    }
}
