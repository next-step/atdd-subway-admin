package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private final List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		sections.add(section);
	}

	public int size() {
		return sections.size();
	}

	public List<Station> getAllStationsBySections() {
		sections.sort(Comparator.comparingInt(Section::getSequence));
		List<Station> stations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
		stations.add(0, sections.stream()
			.findFirst()
			.orElseThrow()
			.getUpStation());
		return stations;
	}
}
