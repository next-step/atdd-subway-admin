package nextstep.subway.station.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.vo.Name;

@Entity
public class Station extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Name name;

	protected Station() {
	}

	public Station(String name) {
		this.name = Name.generate(name);
	}

	public void update(Station station) {
		this.name = Name.generate(station.name());
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name.value();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (!(object instanceof Station))
			return false;
		Station station = (Station)object;
		return Objects.equals(id, station.id) && Objects.equals(name, station.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
