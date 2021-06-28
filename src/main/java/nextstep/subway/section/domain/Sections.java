package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

/**
 *
 * @author heetaek.kim
 */
@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private final List<Section> sections = new LinkedList<>();

	public Sections() {
	}

	public List<Station> serializeStations() {
		List<Station> stations = new LinkedList<>();
		sections.forEach(s -> s.appendStations(stations));
		return stations;
	}

	public Section add(Line line, Station upStation, Station downStation, int distance) {
		Section newSection = new Section(line, upStation, downStation, distance);
		sections.add(newSection);
		return newSection;
	}
}
