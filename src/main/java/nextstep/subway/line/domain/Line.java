package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.domain.Color;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationGroup;

@Entity
public class Line extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Name name;

	@Embedded
	private Color color;

	@Embedded
	private StationGroup stations;

	protected Line() {
	}

	public Line(String name, String color) {
		this(name, color, new StationGroup());
	}

	public Line(String name, String color, StationGroup stationGroup) {
		this.name = Name.generate(name);
		this.color = Color.generate(color);
		this.stations = stationGroup;
	}

	public void update(Line line) {
		this.name = Name.generate(line.name());
		this.color = Color.generate(line.color());
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name.value();
	}

	public String color() {
		return color.value();
	}

	public List<Station> stations() {
		return stations.stations();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		Line line = (Line)object;
		return Objects.equals(id, line.id)
			&& Objects.equals(name, line.name)
			&& Objects.equals(color, line.color)
			&& Objects.equals(stations, line.stations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, color, stations);
	}
}
