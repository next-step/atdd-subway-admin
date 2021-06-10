package nextstep.subway.station.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {

	@Column(name = "name", unique = true)
	private String name;

	public Station() {
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
		return Objects.equals(name, station.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "Station{" +
			"id=" + id +
			", createdDate=" + createdDate +
			", modifiedDate=" + modifiedDate +
			", name='" + name + '\'' +
			'}';
	}
}
