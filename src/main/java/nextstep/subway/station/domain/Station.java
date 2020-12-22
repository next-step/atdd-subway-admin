package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

	public Station(String name) {
		this.name = name;
	}
}
