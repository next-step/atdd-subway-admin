package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

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
		Section section = findFirstSection();
		if (section == null) {
			return Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		stations.add(section.getFront());
		while (section != null) {
			stations.add(section.getBack());
			section = findSectionByFrontStation(sections, section.getBack());
		}

		return stations;
	}

	@Nullable
	private Section findFirstSection() {
		Section nextSection = this.sections.stream().findAny().orElse(null);
		Section firstSection = nextSection;
		while (nextSection != null) {
			firstSection = nextSection;
			nextSection = findSectionByBackStation(this.sections, nextSection.getFront());
		}

		return firstSection;
	}

	@Nullable
	private static Section findSectionByFrontStation(List<Section> sections, Station station) {
		return sections.stream()
				.filter(section -> Objects.equals(section.getFront(), station))
				.findFirst()
				.orElse(null);
	}

	@Nullable
	private static Section findSectionByBackStation(List<Section> sections, Station station) {
		return sections.stream()
				.filter(section -> Objects.equals(section.getBack(), station))
				.findFirst()
				.orElse(null);
	}

}
