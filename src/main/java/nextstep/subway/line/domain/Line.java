package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.domain.Color;
import nextstep.subway.common.domain.Name;
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
	private StationGroup stationGroup;

	protected Line() {
	}

	public Line(String name, String color) {
		this(name, color, new StationGroup());
	}

	public Line(String name, String color, StationGroup stationGroup) {
		this.name = Name.generate(name);
		this.color = Color.generate(color);
		this.stationGroup = stationGroup;
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

	public StationGroup stationGroup() {
		return stationGroup;
	}

	public void changeStationGroup(StationGroup stationGroup) {
		this.stationGroup = stationGroup;
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
			&& Objects.equals(stationGroup, line.stationGroup);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, color, stationGroup);
	}
}
