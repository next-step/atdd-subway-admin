package nextstep.subway.domain.line;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidSectionAddException;

@Embeddable
public class Sections {

	private static final String NOT_INCLUDE_UP_DOWN_STATION_ERROR_MESSAGE = "등록할 수 없는 구간입니다.";
	private static final String SAME_UP_DOWN_STATION_ERROR_MESSAGE = "이미 등록된 구간입니다.";
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new LinkedList<>();

	protected Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public static Sections initialSections(Section section) {
		List<Section> sections = new LinkedList<>();
		sections.add(section);
		return new Sections(sections);
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Station> allUpStations() {
		return this.sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());
	}

	public List<Station> allDownStations() {
		return this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
	}

	public List<Station> allStations() {
		LinkedHashSet<Station> stations = new LinkedHashSet<>();
		stations.addAll(allUpStations());
		stations.addAll(allDownStations());
		return new LinkedList<>(stations);
	}

	public Station firstUpStation() {
		List<Station> stations = allUpStations();
		stations.removeAll(allDownStations());
		return stations.get(0);
	}

	public Station lastDownStation() {
		List<Station> stations = allDownStations();
		stations.removeAll(allUpStations());
		return stations.get(0);
	}

	public void add(Section section) {
		validateAddSection(section);
		sections.forEach(it -> it.rearrange(section));
		sections.add(section);
	}

	private void validateAddSection(Section section) {
		validateSameUpDownStation(section);
		validateNotIncludeUpDownStation(section);
	}

	private void validateNotIncludeUpDownStation(Section section) {
		if (notIncludeUpDownStation(section)) {
			throw new InvalidSectionAddException(NOT_INCLUDE_UP_DOWN_STATION_ERROR_MESSAGE);
		}
	}

	private void validateSameUpDownStation(Section section) {
		if (isSameUpDownStation(section)) {
			throw new InvalidSectionAddException(SAME_UP_DOWN_STATION_ERROR_MESSAGE);
		}
	}

	private boolean notIncludeUpDownStation(Section section) {
		return notIncludeDownStation(section) && notIncludeUpStation(section);
	}

	private boolean notIncludeDownStation(Section section) {
		return !allStations().contains(section.getUpStation());
	}

	private boolean notIncludeUpStation(Section section) {
		return !allStations().contains(section.getDownStation());
	}

	private boolean isSameUpDownStation(Section newSection) {
		return sections.stream()
			.filter(section -> section.isSameUpStation(newSection))
			.anyMatch(section -> section.isSameDownStation(newSection));
	}
}
