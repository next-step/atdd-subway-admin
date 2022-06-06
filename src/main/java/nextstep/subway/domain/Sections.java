package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	private final int NOT_FOUND = -1;

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

	public void add(Line line, Station upStation, Station downStation, int distance) {
		if(sections.isEmpty()) {
			sections.add(new Section(line, upStation, downStation, distance));
			return;
		}

		int index = searchUpStationOrDownStation(upStation, downStation);

		if(index == NOT_FOUND) {
			return;
		}

		createSectionInMiddle(line, upStation, downStation, distance, index);
	}

	private void createSectionInMiddle(Line line, Station upStation, Station downStation, int distance, int index) {
		Section foundSection = sections.get(index);

		/** A - B
		 *  A  ---  C
		 */
		if(foundSection.isUpStation(upStation)) {
			Section updatingSection = new Section(line, downStation, foundSection.getDownStation(),
				foundSection.getDistance() - distance);

			updateSection(index, updatingSection);
		}

		/**     B - C
		 *  A  ---  C
		 */
		if(foundSection.isDownStation(downStation)) {
			Section updatingSection = new Section(line, foundSection.getUpStation(), upStation,
				foundSection.getDistance() - distance);

			updateSection(index, updatingSection);
		}

		sections.add(new Section(line, upStation, downStation, distance));
	}

	private void updateSection(int index, Section updatingSection) {
		sections.remove(index);
		sections.add(updatingSection);
	}

	private int searchUpStationOrDownStation(Station upStation, Station downStation) {
		return IntStream.range(0, sections.size())
			.filter(idx -> sections.get(idx).isUpStation(upStation) || sections.get(idx).isDownStation(downStation))
			.findFirst()
			.orElse(NOT_FOUND);
	}

	private Optional<Section> findNextSection(Station station) {
		return sections.stream()
			.filter(it -> it.isUpStation(station))
			.findFirst();
	}
}
