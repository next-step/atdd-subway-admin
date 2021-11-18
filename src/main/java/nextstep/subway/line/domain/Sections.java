package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void add(Section section) {
		sections.add(section);
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Station> getStations() {
		Station startStation = findStartStation();
		return getOrderedStations(startStation);
	}

	private List<Station> getOrderedStations(Station startStation) {
		Map<Station, Station> upDownStations = getUpDownRoute();
		List<Station> stations = new ArrayList<>();
		Station nextStation = startStation;
		while (upDownStations.containsKey(nextStation)) {
			stations.add(nextStation);
			stations.add(upDownStations.get(nextStation));
			nextStation = upDownStations.get(nextStation);
		}
		return stations;
	}

	private Map<Station, Station> getUpDownRoute() {
		return sections.stream()
			.collect(Collectors.toMap(entry -> entry.getUpStation(), entry -> entry.getDownStation(),
				(o1, o2) -> o1, HashMap::new));
	}

	private Station findStartStation() {
		Set<Station> downStations = sections.stream().map(it -> it.getDownStation()).collect(Collectors.toSet());
		Optional<Station> notDownStation = sections.stream()
			.map(it -> it.getUpStation())
			.filter(it -> !downStations.contains(it))
			.findFirst();
		return notDownStation.orElseThrow(() -> new IllegalStateException("구간 정보가 올바르지 않습니다."));
	}
}
