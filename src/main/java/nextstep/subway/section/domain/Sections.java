package nextstep.subway.section.domain;

import static java.util.stream.Collectors.*;

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

	public List<Station> orderedStations() {
		return sections.stream()
			.flatMap(Section::stations)
			.distinct()
			.collect(toList());
	}

	public void add(Section section) {
		sections.add(section);
	}
}
