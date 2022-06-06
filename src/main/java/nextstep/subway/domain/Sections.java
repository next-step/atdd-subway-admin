package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public List<Station> getStations() {
		List<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		Section firstSection = sections.stream()
			.filter(it -> !downStations.contains(it.getUpStation()))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);

		List<Station> stations = new ArrayList<>();
		stations.add(firstSection.getUpStation());
		stations.add(firstSection.getDownStation());

		Optional<Section> nextSection = findNextSection(firstSection.getDownStation());

		while(nextSection.isPresent()) {
			Station station = nextSection.get().getDownStation();

			stations.add(station);
			nextSection = findNextSection(station);
		}

		return stations;
	}

	public void addSection(Line line, Station upStation, Station downStation, int distance) {
		sections.add(new Section(line, upStation, downStation, distance));
	}

	private Optional<Section> findNextSection(Station station) {
		return sections.stream()
			.filter(it -> it.isUpStation(station))
			.findFirst();
	}
}
