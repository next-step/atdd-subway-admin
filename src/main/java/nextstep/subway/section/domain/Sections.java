package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SubwayLogicException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

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
		Section currentSection = this.getFirstSection();
		Stream<Station> stations = Stream.empty();
		do {
			stations = Stream.concat(stations,
				Stream.of(currentSection.getUpStation(), currentSection.getDownStation()))
				.distinct();
		} while (hasNext(currentSection));

		return stations.collect(Collectors.toList());
	}

	private boolean hasNext(Section currentSection) {
		return this.sections.stream()
			.anyMatch(section -> section.getUpStation().equals(currentSection.getDownStation()));
	}

	private Section getFirstSection() {
		List<Station> downStations = this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return this.sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findFirst().orElseThrow(() -> new SubwayLogicException("노선 설정이 잘못되었습니다."));
	}

}
