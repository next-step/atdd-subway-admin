package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void add(Section section) {
		sections.add(section);
	}

	public void add(Sections sectionGroup) {
		sections.addAll(sectionGroup.sections);
	}

	public List<Station> getStationsList() {
		Map<Station, Station> map = new HashMap<>();
		sections.forEach(section -> map.put(section.getUpStation(), section.getDownStation()));

		List<Station> stations = new ArrayList<>();
		Station station = findFirstStation();
		while (map.get(station) != null) {
			stations.add(station);
			station = map.get(station);
		}
		stations.add(station);
		return stations;
	}

	private Station findFirstStation() {
		List<Station> upStations = sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());
		List<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return upStations.stream()
			.filter(upStation -> !downStations.contains(upStation))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("잘못된 구간 정보입니다."));
	}

	public boolean contains(Section section) {
		return sections.contains(section);
	}
}
