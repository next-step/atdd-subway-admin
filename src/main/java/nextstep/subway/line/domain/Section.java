package nextstep.subway.line.domain;

import static javax.persistence.FetchType.*;

import java.util.stream.Stream;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.station.domain.Station;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"up_station_id", "down_station_id"}))
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Embedded
	private Distance distance;

	protected Section() { }

	Section(Line line, Station upStation, Station downStation, Integer distance) {
		this(null, line, upStation, downStation, distance);
	}

	Section(Long id, Line line, Station upStation, Station downStation, Integer distance) {
		this.id = id;
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = Distance.valueOf(distance);
	}

	boolean isPartOf(Line line) {
		return this.line.equals(line);
	}

	boolean isPreviousOf(Section otherSection) {
		return this.downStation.equals(otherSection.upStation);
	}

	boolean isNextOf(Section otherSection) {
		return this.upStation.equals(otherSection.downStation);
	}

	Stream<Station> getStreamOfStations() {
		return Stream.of(upStation, downStation);
	}
}
