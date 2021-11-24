package nextstep.subway.station.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {

	@Column(unique = true)
	private String name;

	protected Station() {
	}

	public Station(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Station station = (Station)o;
		return Objects.equals(getId(), station.getId()) && Objects.equals(name, station.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
