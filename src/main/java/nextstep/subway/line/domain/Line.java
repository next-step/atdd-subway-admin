package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "line")
    private List<Station> stations;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = new Name(name);
        this.color = color;
    }

    public void update(Line line) {
        this.name = new Name(line.getName());
        this.color = line.getColor();
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
}
