package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public List<Station> stationsInOrder() {
		List<Station> stations = new ArrayList<>();
		for (Section section : sections) {
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		}

		return stations;
	}

	public void add(Section section) {
		this.sections.add(section);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Sections sections1 = (Sections) o;
		if (this.sections.size() != sections1.sections.size()) {
			return false;
		}

		return this.sections.containsAll(sections1.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}
}
