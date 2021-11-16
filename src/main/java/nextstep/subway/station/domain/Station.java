package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {

	@Column(unique = true)
	private String name;

	public Station() {
	}

	public Station(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
