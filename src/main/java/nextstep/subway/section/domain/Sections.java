package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	List<Section> sections = new LinkedList<>();

	public Sections() {

	}

	public void add(Section section) {
		this.sections.add(section);
	}

	public void remove(Section section) {
		this.sections.remove(section);
	}

	public List<Station> toStations() {
		return this.sections.stream()
			.map(section -> section.toStations())
			.flatMap(station -> station.stream())
			.distinct()
			.collect(Collectors.toList());
	}

}
