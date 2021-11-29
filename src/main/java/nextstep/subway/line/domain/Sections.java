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
		if (IsSectionsEmpty()) {
			sections.add(section);
			return;
		}

		addSectionsFirstLocation(section);

	}

	private void addSectionsFirstLocation(Section section) {
		Section firstSection = sections.stream()
			.filter(s -> s.getSequence() == 1)
			.filter(s -> s.getUpStation().equals(section.getDownStation()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("역이 존재하지 않습니다."));
		section.updateSequence(firstSection.getSequence());
		firstSection.updateSequence(section.getSequence() + 1);
		sections.add(section.getSequence() - 1, section);
	}

	private boolean IsSectionsEmpty() {
		return sections.size() == 0;
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
			.orElseThrow(() -> new IllegalArgumentException("구간이 존재하지 않습니다."))
			.getUpStation());
		return stations;
	}
}
