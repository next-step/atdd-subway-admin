package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;

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

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void addSection(Section section) {
        sections.addSection(section);
        section.setLine(this);
    }

    // public List<Station> getStations() {
    //     List<Station> stations = new ArrayList<>();
    //     Station station = getStartStation();
    //
    //     while (!station.isLast()) {
    //         stations.add(station);
    //         station = station.nextStation();
    //     }
    //
    //     stations.add(station);
    //
    //     return unmodifiableList(stations);

    // }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
    // private Station getStartStation() {
    //     return sections.stream()
    //         .map(Section::getUpStation)
    //         .filter(Station::isFirst)
    //         .findAny()
    //         .orElseThrow(() -> new LineEndpointException("비 정상적인 노선입니다. 출발역이 존재하지 않습니다."));

    // }
}
