package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SubwayLogicException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	private static final String INVALID_SECTION_MESSAGE = "노선 설정이 잘못되었습니다.";
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private final List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public void removeSection(Section section) {
		this.sections.remove(section);
	}

	public List<Station> getOrderedStations() {
		Section currentSection = this.findUpStationEndPointSection();
		Stream<Station> stations = this.getStationStream(currentSection);
		do {
			currentSection = this.nextSection(currentSection);
			stations = Stream.concat(stations,
				Optional.of(this.getStationStream(currentSection)).orElse(Stream.empty()))
				.filter(Objects::nonNull)
				.distinct();
		} while (hasNext(currentSection));

		return stations.collect(Collectors.toList());
	}

	private Stream<Station> getStationStream(Section section) {
		return Stream.of(section.getUpStation(), section.getDownStation());
	}

	private boolean hasNext(Section currentSection) {
		return this.sections.stream()
			.anyMatch(section -> section.getUpStation().equals(currentSection.getDownStation()));
	}

	private Section nextSection(Section currentSection) {
		return this.sections.stream()
			.filter(section -> section.getUpStation().equals(currentSection.getDownStation()))
			.findAny().orElse(new Section());

	}

	private Section findUpStationEndPointSection() {
		List<Station> downStations = this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return this.sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findFirst().orElseThrow(() -> new SubwayLogicException(INVALID_SECTION_MESSAGE));
	}

}
