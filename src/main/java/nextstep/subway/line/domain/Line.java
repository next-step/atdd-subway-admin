package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Column
    private String color;

    // TODO 일급컬렉션
    @OneToMany(mappedBy = "line")
    private List<Station> stations = new LinkedList<Station>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = new Name(name);
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = new Name(name);
        this.color = color;
        this.stations.add(upStation);
        this.stations.add(downStation);
    }

    public void update(Line line) {
        this.name = new Name(line.getName());
        this.color = line.getColor();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        stations.add(upStation);
        stations.add(downStation);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name.printName();
    }

    public String getColor() {
        return color;
    }

    public List<Station> getSections() {
        return this.stations;
    }
}
