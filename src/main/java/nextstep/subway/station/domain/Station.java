package nextstep.subway.station.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.section.domain.Section;

@Entity
public class Station extends BaseEntity {

	private static final int FIRST_STATION_INDEX = 0;
	private static final int ADJUST_LAST_STATION_INDEX = -1;

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

	public boolean isInnerStation(List<Station> stations) {
		int index = stations.indexOf(this);
		return FIRST_STATION_INDEX < index && index < stations.size() + ADJUST_LAST_STATION_INDEX;
	}

	public boolean isIncludedStation(Section section) {
		if (Objects.isNull(section)) {
			return false;
		}
		return this.equals(section.upStation()) || this.equals(section.downStation());
	}

	public boolean isSameStation(Station station) {
		return this.equals(station);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Station)) {
			return false;
		}
		Station station = (Station)object;
		return Objects.equals(id, station.id) && Objects.equals(name, station.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
