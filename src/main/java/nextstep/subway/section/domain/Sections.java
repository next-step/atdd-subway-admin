package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public Sections() {
	}

	public List<Station> getStations() {
		return sections.stream()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	public void add(Section section) {
		sections.add(section);
	}
}
