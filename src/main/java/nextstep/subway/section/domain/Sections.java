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

	public void add(Section section) {
		if (alreadyExists(section)) {
			throw new IllegalArgumentException("이미 존재하는 구간입니다.");
		}

		if (sections.isEmpty() || section.isEqualToUpStation(findLast()) || section.isEqualToDownStation(findFirst())) {
			sections.add(section);
			return;
		}

		divideSection(section);
	}

	private boolean alreadyExists(Section section) {
		return sections.stream()
			.anyMatch(
				s -> (section.isEqualToUpStation(s.getUpStation()) && section.isEqualToDownStation(s.getDownStation()))
					||
					(section.isEqualToDownStation(s.getUpStation()) && section.isEqualToUpStation(s.getDownStation())));
	}

	private void divideSection(Section section) {
		Section persistSection = sections.stream()
			.filter(s -> s.matchedOnlyOneStationAndIncludedSection(section))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("추가할 수 없습니다."));

		List<Section> sections = persistSection.divide(section);

		this.sections.remove(persistSection);
		this.sections.addAll(sections);
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

	public Station findFirst() {
		if (sections.isEmpty()) {
			throw new IllegalArgumentException("구간이 존재하지 않습니다.");
		}
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

	public Station findLast() {
		if (sections.isEmpty()) {
			throw new IllegalArgumentException("구간이 존재하지 않습니다.");
		}
		Station standardStation = sections.get(0).getDownStation();
		return trackDownStation(standardStation);
	}

	private Station trackDownStation(Station downStation) {
		Station station = getNextStation(downStation);

		if (station == null) {
			return downStation;
		}

		return trackDownStation(station);
	}

	private Station getNextStation(Station upStation) {
		return sections.stream()
			.filter(section -> section.isEqualToUpStation(upStation))
			.map(Section::getDownStation)
			.findFirst()
			.orElse(null);
	}

	private boolean hasNextStation(Station upStation) {
		return sections.stream()
			.anyMatch(section -> section.isEqualToUpStation(upStation));
	}

	public void delete(Section section) {
		sections.remove(section);
	}
}
