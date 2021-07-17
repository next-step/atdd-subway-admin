package nextstep.subway.section.domain;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

/**
 *
 * @author heetaek.kim
 */
@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private final List<Section> sections = new LinkedList<>();

	protected Sections() {}

	public Sections(Section section) {
		if (section == null) {
			throw new IllegalArgumentException("section is null");
		}
		this.sections.add(section);
	}

	public List<Station> orderedStations() {
		return sections.stream()
			.flatMap(Section::stations)
			.distinct()
			.collect(toList());
	}

	public void addSection(Section section) throws SectionCannotAddException, SectionDistanceNotEnoughException {
		// 거리에 대한 validation exception 은 section 추가시 발생한다.
		validateAddable(section);
		for (Section existSection : sections) {
			existSection.appendStations(section);
		}
		this.sections.add(section);
	}

	public List<Section> sections() {
		return Collections.unmodifiableList(this.sections);
	}

	private void validateAddable(Section section) throws SectionCannotAddException {
		this.validateContainsOnlyOneStation(section);
	}

	private void validateContainsOnlyOneStation(Section newSection) throws SectionCannotAddException {
		long matchedContainingOneStation = this.sections.stream()
			.filter(section -> section.containsOneStation(newSection))
			.count();
		if (matchedContainingOneStation != 1L) {
			throw new SectionCannotAddException("failed validate contains only one station");
		}
	}
}
