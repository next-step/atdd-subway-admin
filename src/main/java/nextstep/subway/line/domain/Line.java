package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Station> stations = new ArrayList<>();

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addStations(upStation, downStation);
        sections.add(new Section(this, upStation, downStation, distance));
    }

    private void addStations(Station upStation, Station downStation) {
        if (!this.equals(upStation.getLine())) {
            upStation.addLine(this);
        }
        if (!this.equals(downStation.getLine())) {
            downStation.addLine(this);
        }
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
