package nextstep.subway.line.domain;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	private static final int ORDER_START_SECTION_IDX = 0;

	@OneToMany(fetch = LAZY, mappedBy = "line", cascade = ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {}

	private Sections(List<Section> sections) {
		this.sections = sections;
	}

	static Sections of(Section... sections) {
		return new Sections(asList(sections));
	}

	void add(Section section) {
		this.sections.add(section);
	}

	List<Station> orderedStations() {
		return orderedSections()
			.flatMap(Section::getStreamOfStations)
			.distinct()
			.collect(toList());
	}

	private Stream<Section> orderedSections() {
		Section currentSection = sections.get(ORDER_START_SECTION_IDX);
		return Stream.concat(
			findPreviousSectionsClosed(currentSection),
			findNextSectionsClosed(currentSection))
			.distinct();
	}

	private Stream<Section> findPreviousSectionsClosed(Section currentSection) {
		Optional<Section> previousSection = sections.stream()
			.filter(section -> section.isPreviousOf(currentSection))
			.findFirst();

		Stream<Section> current = Stream.of(currentSection);

		return previousSection
			.map(section -> Stream.concat(findPreviousSectionsClosed(section), current))
			.orElse(current);
	}

	private Stream<Section> findNextSectionsClosed(Section currentSection) {
		Optional<Section> nextSection = sections.stream()
			.filter(section -> section.isNextOf(currentSection))
			.findFirst();

		Stream<Section> current = Stream.of(currentSection);

		return nextSection
			.map(section -> Stream.concat(current, findNextSectionsClosed(section)))
			.orElse(current);
	}
}

