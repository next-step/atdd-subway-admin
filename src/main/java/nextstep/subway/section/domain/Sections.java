package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
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
		List<Station> stations = new LinkedList<>();
		this.sections.stream()
			.forEach(section -> stations.addAll(section.toStations()));
		return stations;
	}

}
