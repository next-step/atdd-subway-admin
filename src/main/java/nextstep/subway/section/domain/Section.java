package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"line_id", "up_station_id", "down_station_id"}))
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	private int distance;

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public List<Station> getStations() {
		return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
	}
}
