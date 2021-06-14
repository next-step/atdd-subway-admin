package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
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

    public void addSection(Section section) {
        section.toLine(this);
        sections.add(section);
    }

    public List<Station> getStationNames() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }
}
