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
	private static final int MIN_SIZE = 1;

	@OneToMany(fetch = LAZY, mappedBy = "line", cascade = ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {}

	private Sections(List<Section> sections) {
		this.sections = sections;
	}

	static Sections of(Section... sections) {
		return new Sections(new ArrayList<>(asList(sections)));
	}

	void addSection(Section otherSection) {
		checkValidation(otherSection);

		for (Section section : sections) {
			section.addInnerSection(otherSection);
		}

		sections.add(otherSection);
	}

	void removeSectionBy(Station station) {
		validateMinSize();

		Section sectionToRemove = findSectionToRemoveBy(station);

		removeConnectingStation(station, sectionToRemove);

		sections.remove(sectionToRemove);
	}

	Distance sumDistance() {
		return sections.stream()
			.map(Section::getDistance)
			.reduce(Distance::plus)
			.orElseThrow(IllegalStateException::new);
	}

	List<Station> orderedStations() {
		return orderedSections()
			.flatMap(Section::getStreamOfStations)
			.distinct()
			.collect(toList());
	}

	private void removeConnectingStation(Station station, Section sectionToRemove) {
		for (Section section : sections) {
			section.removeConnectingStation(station, sectionToRemove);
		}
	}

	private Stream<Section> orderedSections() {
		Section currentSection = sections.get(ORDER_START_SECTION_IDX);
		return Stream.concat(
			findUpwardSectionsClosed(currentSection),
			findDownwardSectionsClosed(currentSection))
			.distinct();
	}

	private Stream<Section> findUpwardSectionsClosed(Section currentSection) {
		Optional<Section> previousSection = sections.stream()
			.filter(section -> section.isUpwardOf(currentSection))
			.findFirst();

		Stream<Section> current = Stream.of(currentSection);

		return previousSection
			.map(section -> Stream.concat(findUpwardSectionsClosed(section), current))
			.orElse(current);
	}

	private Stream<Section> findDownwardSectionsClosed(Section currentSection) {
		Optional<Section> nextSection = sections.stream()
			.filter(section -> section.isDownwardOf(currentSection))
			.findFirst();

		Stream<Section> current = Stream.of(currentSection);

		return nextSection
			.map(section -> Stream.concat(current, findDownwardSectionsClosed(section)))
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
			.anyMatch(section -> section.hasEqualUpStation(otherSection));

		return sections.stream()
			.anyMatch(section -> existsSameUpStation && section.hasEqualDownStation(otherSection));
	}

	private boolean notExistsUpAndDownStations(Section otherSection) {
		return sections.stream()
			.flatMap(Section::getStreamOfStations)
			.noneMatch(otherSection::contain);
	}

	private Section findSectionToRemoveBy(Station station) {
		return sections.stream()
			.filter(section -> section.contain(station))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("노선에 없는 역의 구간은 제거할 수 없습니다."));
	}

	private void validateMinSize() {
		if (sections.size() == MIN_SIZE) {
			throw new IllegalArgumentException("노선의 남은 구간이 하나 밖에 없어 제거할 수 없습니다.");
		}
	}
}