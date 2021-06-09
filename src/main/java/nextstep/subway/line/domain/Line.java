package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.common.BaseEntity;
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
	private List<Station> stations = new ArrayList<>();

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

	public List<String> getStationNames() {
		return stations.stream().map(Station::getName).collect(toList());
	}
}
