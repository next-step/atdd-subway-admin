package nextstep.subway.station.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	protected Station() {
	}

	public static Station of(String name) {
		Station station = new Station();
		station.name = name;
		return station;
	}

	public static Station of(Long id, String name) {
		Station station = new Station();
		station.id = id;
		station.name = name;
		return station;
	}

	public Long getId() {
		return id;
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

		if (!Objects.equals(id, station.id)) {
			return false;
		}
		return Objects.equals(name, station.name);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
