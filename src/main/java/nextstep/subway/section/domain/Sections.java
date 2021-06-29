package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;

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

	public void add(Section section){
		this.sections.add(section);
	}

	public List<Station> getStations(){
		List<Station> stations = new ArrayList<>();
		for (Section section : sections){
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		}

		return stations;
	}
}
