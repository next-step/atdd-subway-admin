package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
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
	public static final int EMPTY_REMOVABLE_SECTION_COUNT = 0;
	public static final int SECTION_COUNT_ONE = 1;

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
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

		this.rebuildSection(newSection);
	}

	private void rebuildSection(Section newSection) {
		this.sections.stream()
			.filter(section -> section.isBuildable(newSection))
			.findAny().ifPresent(section -> section.rebuildStation(newSection));
		this.sections.add(newSection);
	}

	private boolean isEntryPointSection(Section newSection) {
		List<Station> orderedStations = this.getOrderedStations();
		Station firstUpStation = orderedStations.get(0);
		Station lastDownStation = orderedStations.get(orderedStations.size() - 1);

		return this.isUpStationEntryPointSection(newSection, firstUpStation) || this.isDownStationEntryPointSection(
			newSection, lastDownStation);
	}

	private boolean isDownStationEntryPointSection(Section newSection, Station lastDownStation) {
		return lastDownStation.equals(newSection.getUpStation());
	}

	private boolean isUpStationEntryPointSection(Section newSection, Station firstUpStation) {
		return firstUpStation.equals(newSection.getDownStation());
	}

	private void validateAlreadyExistSection(Section newSection) {
		List<Station> orderedStations = this.getOrderedStations();
		if (orderedStations.containsAll(Arrays.asList(newSection.getUpStation(), newSection.getDownStation()))) {
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

	public void removeSection(Station station) {
		this.validateRemovable(station);

		Optional<Section> upSectionOpt = this.findUpSectionByDownStation(station);
		Optional<Section> downSectionOpt = this.findDownSectionByUpStation(station);
		if (upSectionOpt.isPresent() && downSectionOpt.isPresent()) {
			this.addCombineSection(upSectionOpt.get(), downSectionOpt.get());
		}
		upSectionOpt.ifPresent(this.sections::remove);
		downSectionOpt.ifPresent(this.sections::remove);
	}

	private void addCombineSection(Section upSection, Section downSection) {
		Section combineSection = new Section(upSection.getLine(), upSection.getUpStation(),
			downSection.getDownStation(),
			upSection.combineSectionDistance(downSection));
		this.sections.add(combineSection);
	}

	private Optional<Section> findDownSectionByUpStation(Station station) {
		return this.sections.stream().filter(section -> section.getUpStation().equals(station)).findAny();
	}

	private Optional<Section> findUpSectionByDownStation(Station station) {
		return this.sections.stream().filter(section -> section.getDownStation().equals(station)).findAny();
	}

	private void validateRemovable(Station station) {
		int sectionSize = this.sections.size();
		long removableSectionsCount = this.getRemovableSections(station);
		if ((sectionSize == SECTION_COUNT_ONE) && (sectionSize == removableSectionsCount)) {
			throw new SubwayLogicException("노선의 유일한 구간으로 제거할 수 없습니다.");
		}

		if (removableSectionsCount == EMPTY_REMOVABLE_SECTION_COUNT) {
			throw new SubwayLogicException("제거 가능한 구간이 없습니다");
		}
	}

	private long getRemovableSections(Station station) {
		return this.sections.stream()
			.filter(section -> section.containStation(station))
			.count();
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
		if (section == null) {
			return Stream.empty();
		}
		return Stream.of(section.getUpStation(), section.getDownStation());
	}

	private boolean hasNext(Section currentSection) {
		if (currentSection == null) {
			return false;
		}
		return this.sections.stream()
			.anyMatch(section -> section.getUpStation().equals(currentSection.getDownStation()));
	}

	private Section nextSection(Section currentSection) {
		return this.sections.stream()
			.filter(section -> section.getUpStation().equals(currentSection.getDownStation()))
			.findAny().orElse(null);
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
