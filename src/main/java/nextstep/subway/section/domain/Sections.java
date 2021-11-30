package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void add(Section section) {
		sections.add(section);
	}

	public List<Station> getStationsUpToDown() {
		List<Station> result = new ArrayList<>();
		if (sections.isEmpty()) {
			return result;
		}
		result.add(getTopStation());
		result.addAll(getDownStationList());

		return result;
	}

	private List<Station> getDownStationList() {
		return sections.stream().map(Section::getDownStation).collect(Collectors.toList());
	}

	private Station getTopStation() {
		return sections.get(0).getUpStation();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Sections sections1 = (Sections)o;
		return Objects.equals(sections, sections1.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}

	@Override
	public String toString() {
		return "Sections{" +
			"sections=" + sections +
			'}';
	}
}
