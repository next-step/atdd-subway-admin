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
		return new Sections(new ArrayList<>(asList(sections)));
	}

	void add(Section otherSection) {
		checkValidation(otherSection);
		for (Section section : sections) {
			section.addSectionBetween(otherSection);
		}
		sections.add(otherSection);
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
			findUpDirectionSectionsClosed(currentSection),
			findDownDirectionSectionsClosed(currentSection))
			.distinct();
	}

	private Stream<Section> findUpDirectionSectionsClosed(Section currentSection) {
		Optional<Section> previousSection = sections.stream()
			.filter(section -> section.isUpDirectionOf(currentSection))
			.findFirst();

		Stream<Section> current = Stream.of(currentSection);

		return previousSection
			.map(section -> Stream.concat(findUpDirectionSectionsClosed(section), current))
			.orElse(current);
	}

	private Stream<Section> findDownDirectionSectionsClosed(Section currentSection) {
		Optional<Section> nextSection = sections.stream()
			.filter(section -> section.isDownDirectionOf(currentSection))
			.findFirst();

		Stream<Section> current = Stream.of(currentSection);

		return nextSection
			.map(section -> Stream.concat(current, findDownDirectionSectionsClosed(section)))
			.orElse(current);
	}

	private void checkValidation(Section otherSection) {
		if (sections.isEmpty()) {
			return;
		}
		if (exists(otherSection)) {
			throw new IllegalArgumentException("해당 구간은 이미 존재합니다.");
		}
		if (notExistsUpAndDownStations(otherSection)) {
			throw new IllegalArgumentException("상행역 하행역 모두 노선에 존재하지 않는 구간은 등록할 수 없다.");
		}
	}

	private boolean exists(Section otherSection) {
		boolean existsSameUpStation = sections.stream()
			.anyMatch(section -> section.isSameUpStation(otherSection));

		return sections.stream()
			.anyMatch(section -> existsSameUpStation && section.isSameDownStation(otherSection));
	}

	private boolean notExistsUpAndDownStations(Section otherSection) {
		return sections.stream()
			.flatMap(Section::getStreamOfStations)
			.noneMatch(otherSection::contains);
	}
}