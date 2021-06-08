package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void add(final Section section) {
		sections.add(section);
	}

	public boolean contain(final Section section) {
		return sections.contains(section);
	}

	public List<Station> stationsBySorted() {
		return this.sections.stream()
							.flatMap(Section::streamOfStation)
							.distinct()
							.collect(Collectors.toList());
	}

}
