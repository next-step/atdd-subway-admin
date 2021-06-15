package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Transactional
public class Sections {
	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public List<Station> getStationsInSections() {
		List<Station> stationsInOrder = new ArrayList<>();
		Station firstStation = findFirstStation();
		stationsInOrder.add(firstStation);

		while (doesExistNextStation(firstStation)) {
			firstStation = findNextStation(firstStation);
			stationsInOrder.add(firstStation);
		}
		return stationsInOrder;
	}

	private Station findNextStation(Station station) {
		return sections.stream().filter(section -> section.getUpStation().equals(station))
				.findFirst().get()
				.getDownStation();
	}

	private boolean doesExistNextStation(Station station) {
		return sections.stream().anyMatch(section -> section.getUpStation().equals(station));
	}

	private Station findFirstStation() {
		Station firstStation = null;
		for (Section section : sections) {
			firstStation = findRightUpStation(firstStation, section);
		}
		return firstStation;
	}

	private Station findRightUpStation(Station firstStation, Section section) {
		if (section.getDownStation().equals(firstStation) || firstStation == null) {
			firstStation = section.getUpStation();
		}
		return firstStation;
	}

	public void addSection(Section section) {
		sections.stream().filter(existedSection -> existedSection.getUpStation().equals(section.getUpStation()))
				.findFirst()
				.ifPresent(it -> it.updateUpStation(section.getDownStation()));

		sections.stream().filter(existedSection -> existedSection.getDownStation().equals(section.getDownStation()))
				.findFirst()
				.ifPresent(existedSection -> existedSection.updateDownStation(section.getUpStation()));

		sections.add(section);
	}
}
