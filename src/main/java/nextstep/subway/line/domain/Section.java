package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.application.SectionValidationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "section")
public class Section extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "line_id", nullable = false)
	private Line line;

	@ManyToOne
	@JoinColumn(name = "front_station_id", nullable = false)
	private Station front;

	@ManyToOne
	@JoinColumn(name = "back_station_id", nullable = false)
	private Station back;

	@Embedded
	private Distance distance;

	public Section() {
	}

	public Section(Line line, Station front, Station back, int distance) {
		this(line, front, back, new Distance(distance));
	}

	public Section(Line line, Station front, Station back, Distance distance) {
		this.line = line;
		this.front = front;
		this.back = back;
		this.distance = distance;
	}

	public boolean containsAllStation(Section section) {
		return isStationContains(section.front) && isStationContains(section.back);
	}

	public boolean containsAnyStation(Section section) {
		return isStationContains(section.front) || isStationContains(section.back);
	}

	private boolean isStationContains(Station station) {
		return this.front.equals(station) || this.back.equals(station);
	}

	public List<Section> splitSection(Section section) {
		if (!isCanSplit(section)) {
			throw new SectionValidationException("cannot split this section !");
		}

		Distance newSectionDistance = calculateDistance(section);
		Section newSection = Optional.of(section)
				.filter(s -> isFrontEqual(s.front))
				.map(s -> new Section(this.line, section.back, this.back, newSectionDistance))
				.orElseGet(() -> new Section(this.line, this.front, section.front, newSectionDistance));

		return Arrays.asList(newSection, section);
	}

	public boolean isCanSplit(Section section) {
		return isFrontEqual(section.front) ^ isBackEqual(section.back);
	}

	public boolean isFrontEqual(Station station) {
		return this.front.equals(station);
	}

	public boolean isBackEqual(Station station) {
		return this.back.equals(station);
	}

	private Distance calculateDistance(Section section) throws SectionValidationException {
		Distance newSectionDistance;
		try {
			newSectionDistance = this.distance.minus(section.distance);
		} catch (IllegalArgumentException e) {
			throw new SectionValidationException("distance over");
		}
		return newSectionDistance;
	}

	public Line getLine() {
		return line;
	}

	public Station getFront() {
		return front;
	}

	public Station getBack() {
		return back;
	}

	public Distance getDistance() {
		return distance;
	}
}
