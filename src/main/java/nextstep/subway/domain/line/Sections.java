package nextstep.subway.domain.line;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.domain.station.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new LinkedList<>();

	protected Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public static Sections initialSections(Section section) {
		List<Section> sections = new LinkedList<>();
		sections.add(section);
		return new Sections(sections);
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Station> allUpStations() {
		return this.sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());
	}

	public List<Station> allDownStations() {
		return this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
	}

	public List<Station> allStations() {
		LinkedHashSet<Station> stations = new LinkedHashSet<>();
		stations.addAll(allUpStations());
		stations.addAll(allDownStations());
		return new LinkedList<>(stations);
	}

}
