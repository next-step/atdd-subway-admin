package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.domain.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Station extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;

	@Transient
	private int nextDistance;

	public Station(String name) {
		this.name = name;
	}

	public int getNextDistance() {
		return nextDistance;
	}

	public void updateNextDistance(int distance) {
		this.nextDistance = distance;
	}

	public boolean isFinalStation() {
		return this.nextDistance == 0;
	}
}
