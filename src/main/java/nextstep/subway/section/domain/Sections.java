package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private final List<Section> sections = new LinkedList<>();

	public Sections() {

	}

	public void add(Section addedSection) {
		if (sections.isEmpty()) {
			sections.add(addedSection);
		}

		Section persistSection = sections.stream()
			.filter(section -> section.matchedOnlyOneStation(addedSection))
			.findFirst()
			.orElse(null);

		if (persistSection != null) {
			List<Section> sections = persistSection.divide(addedSection);

			this.sections.remove(persistSection);
			this.sections.addAll(sections);
		}
	}

	public List<Station> stationsFromUpToDown() {
		return sections.stream()
			.flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
			.collect(Collectors.toList());
	}
}
