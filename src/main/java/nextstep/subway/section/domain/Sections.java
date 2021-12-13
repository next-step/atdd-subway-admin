package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void addSection(Section section) {
		sections.add(section);
	}

	public List<Station> getStations() {
		return sections.stream()
			.flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
			.collect(Collectors.toList());
	}
}
