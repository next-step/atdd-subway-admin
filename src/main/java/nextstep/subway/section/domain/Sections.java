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

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private final List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void addSection(Section newSection) {
		if (this.sections.isEmpty() || this.isEntryPointSection(newSection)) {
			this.sections.add(newSection);
			return;
		}

		this.validateAlreadyExistSection(newSection);
		this.validateStationsNotContained(newSection);

		this.rebuildSectionByUpStation(newSection);
		this.rebuildSectionByDownStation(newSection);
		this.sections.add(newSection);
	}

	private void rebuildSectionByUpStation(Section newSection) {
		this.sections.stream()
			.filter(section -> section.getUpStation().equals(newSection.getUpStation()))
			.findAny().ifPresent(section -> {
			section.validateSectionDistance(newSection);
			section.rebuildUpstation(newSection);
		});
	}

	private void rebuildSectionByDownStation(Section newSection) {
		this.sections.stream()
			.filter(section -> section.getDownStation().equals(newSection.getDownStation()))
			.findAny().ifPresent(section -> {
			section.validateSectionDistance(newSection);
			section.rebuildDownStation(newSection);
		});
	}

	private boolean isEntryPointSection(Section newSection) {
		List<Station> orderedStations = this.getOrderedStations();

		return orderedStations.get(0).equals(newSection.getDownStation()) || orderedStations.get(
			orderedStations.size() - 1).equals(newSection.getUpStation());
	}

	private void validateAlreadyExistSection(Section newSection) {
		List<Station> orderedStations = this.getOrderedStations();
		if (orderedStations.contains(newSection.getDownStation()) && orderedStations.contains(
			newSection.getUpStation())) {
			throw new SubwayLogicException("해당구간은 이미 구성되어있어 추가할 수 없습니다.");
		}
	}

	private void validateStationsNotContained(Section newSection) {
		List<Station> orderedStations = this.getOrderedStations();
		if (!orderedStations.contains(newSection.getDownStation()) && !orderedStations.contains(
			newSection.getUpStation())) {
			throw new SubwayLogicException("해당 구간은 연결 가능한 상행, 하행역이 없습니다.");
		}
	}

	public void removeSection(Section section) {
		this.sections.remove(section);
	}

	public List<Station> getOrderedStations() {
		Optional<Section> currentSection = this.findUpStationEndPointSection();
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

	private Stream<Station> getStationStream(Optional<Section> section) {
		return section.map(value -> Stream.of(value.getUpStation(), value.getDownStation())).orElseGet(Stream::empty);
	}

	private boolean hasNext(Optional<Section> currentSection) {
		return currentSection.filter(value -> this.sections.stream()
			.anyMatch(section -> section.getUpStation().equals(value.getDownStation()))).isPresent();
	}

	private Optional<Section> nextSection(Optional<Section> currentSection) {
		return currentSection.flatMap(value -> this.sections.stream()
			.filter(section -> section.getUpStation().equals(value.getDownStation()))
			.findAny());

	}

	private Optional<Section> findUpStationEndPointSection() {
		List<Station> downStations = this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return this.sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findAny();
	}

}
