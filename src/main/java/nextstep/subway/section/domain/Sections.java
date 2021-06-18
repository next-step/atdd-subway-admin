package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new LinkedList<>();

	public Sections() {

	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void add(Section addedSection) {
		if (sections.isEmpty()) {
			sections.add(addedSection);
		}

		Section persistSection = sections.stream()
			.filter(section -> section.matchedOnlyOneStation(addedSection))
			.findFirst()
			.orElse(null);

		if (persistSection != null) {
			List<Section> sections = persistSection.divide(addedSection);

			this.sections.remove(persistSection);
			this.sections.addAll(sections);
		}
	}

	public List<Station> stationsFromUpToDown() {
		List<Station> stations = new LinkedList<>();
		Station standardStation = findFirst();
		stations.add(standardStation);

		while (hasNextStation(standardStation)) {
			Station nextStation = getNextStation(standardStation);
			stations.add(nextStation);
			standardStation = nextStation;
		}

		return stations;
	}

	private Station findFirst() {
		Station standardStation = sections.get(0).getUpStation();
		return trackUpStation(standardStation);
	}

	private Station trackUpStation(Station upStation) {
		Station station = sections.stream()
			.filter(section -> section.isEqualToDownStation(upStation))
			.map(Section::getUpStation)
			.findFirst()
			.orElse(null);

		if (station == null) {
			return upStation;
		}

		return trackUpStation(station);
	}

	private Station getNextStation(Station upStation) {
		return sections.stream()
			.filter(section -> section.isEqualToUpStation(upStation))
			.map(Section::getDownStation)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("마지막 역입니다."));
	}

	private boolean hasNextStation(Station upStation) {
		return sections.stream()
			.anyMatch(section -> section.isEqualToUpStation(upStation));
	}
}
