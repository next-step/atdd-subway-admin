package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		sections.add(section);
	}

	public List<Station> stations() {
		List<Station> stations = new ArrayList<>();
		for (Section section : sections) {
			stations.addAll(Arrays.asList(section.getUpStation(), section.getDownStation()));
		}
		return stations;
	}
}
