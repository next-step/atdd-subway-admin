package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.AppException;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private final List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	private Sections(List<Section> sections) {
		this.sections.addAll(sections);
	}

	public static Sections of() {
		return new Sections();
	}

	public static Sections of(List<Section> sections) {
		return new Sections(sections);
	}

	public void add(Section newSection) {
		if (sections.isEmpty()) {
			this.sections.add(newSection);
			return;
		}
		update(newSection);
	}

	public boolean contains(Section section) {
		return this.sections.contains(section);
	}

	public List<Station> getOrderedStations() {
		Map<Station, Station> stations = this.sections.stream()
			.collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
		Station firstStation = findFirstStation(stations);
		return sortStations(firstStation, stations);
	}

	private List<Station> sortStations(Station firstStation, Map<Station, Station> stations) {
		List<Station> orderedStations = new ArrayList<>();
		orderedStations.add(firstStation);
		Station station = firstStation;
		while (stations.containsKey(station)) {
			Station downStation = stations.get(station);
			orderedStations.add(downStation);
			station = downStation;
		}
		return orderedStations;
	}

	private Station findFirstStation(Map<Station, Station> stations) {
		return stations.keySet().stream()
			.filter(upStation -> !stations.containsValue(upStation))
			.findFirst()
			.orElseThrow(() ->
				new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "첫 상행선 종점을 찾을 수 없습니다"));
	}

	public void update(Section newSection) {
		validateUpdate(newSection);
		if (isMiddleSection(newSection)) {
			Section targetSection = findByUpStation(newSection.getUpStation());
			targetSection.updateUpStation(newSection);
		}
		this.sections.add(newSection);
	}

	private void validateUpdate(Section newSection) {
		Optional<Section> optionalSection = sections.stream()
			.filter(section ->
				section.getUpStation().equals(newSection.getUpStation()) &&
					section.getDownStation().equals(newSection.getDownStation()))
			.findFirst();
		if (optionalSection.isPresent()) {
			throw new AppException(ErrorCode.WRONG_INPUT, "해당 구간이 이미 존재합니다");
		}
		if (!isContainsAnyStation(newSection)) {
			throw new AppException(ErrorCode.WRONG_INPUT, "해당 구간의 역들은 존재하지 않습니다");
		}
	}

	private boolean isContainsAnyStation(Section section) {
		List<Station> stations = getOrderedStations();
		return stations.contains(section.getUpStation())
			|| stations.contains(section.getDownStation());
	}

	private boolean isMiddleSection(Section section) {
		if (isFirstStation(section.getDownStation())) {
			return false;
		}
		return !isLastStation(section.getUpStation());
	}

	private boolean isLastStation(Station station) {
		List<Station> stations = getOrderedStations();
		if (stations.isEmpty()) {
			return false;
		}
		return stations.get(stations.size() - 1).equals(station);
	}

	private boolean isFirstStation(Station station) {
		List<Station> stations = getOrderedStations();
		if (stations.isEmpty()) {
			return false;
		}
		return stations.get(0).equals(station);
	}

	private Section findByUpStation(Station upStation) {
		return this.sections.stream()
			.filter(section -> section.getUpStation().equals(upStation))
			.findFirst()
			.orElseThrow(() ->
				new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "상행역을 찾을 수 없습니다"));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Sections sections1 = (Sections)o;

		return sections.equals(sections1.sections);
	}

	@Override
	public int hashCode() {
		return sections.hashCode();
	}

}
