package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Embeddable
public class LineSections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections;

	public LineSections() {
	}

	public LineSections(Section... sections) {
		this.sections = new ArrayList<>(Arrays.asList(sections));
	}

	public List<Station> getSortedStations() {
		List<Station> stations = new ArrayList<>();
		Section section = findSectionByFrontStation(sections, null);
		while (section.getBack() != null) {
			stations.add(section.getBack());
			section = findSectionByFrontStation(sections, section.getBack());
		}

		return stations;
	}

	private static Section findSectionByFrontStation(List<Section> sections, @Nullable Station station) {
		return sections.stream()
				.filter(section -> Objects.equals(section.getFront(), station))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("cannot find station"));
	}
}
