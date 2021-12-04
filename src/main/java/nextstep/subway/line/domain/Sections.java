package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections;

	protected Sections() {
	}

	private Sections(List<Section> sections) {
		this.sections = new ArrayList<>(sections);
	}

	public static Sections of() {
		return new Sections(new ArrayList<>());
	}

	public static Sections of(List<Section> sections) {
		return new Sections(sections);
	}

	public void add(Section section) {
		this.sections.add(section);
	}

	public boolean contains(Section section) {
		return this.sections.contains(section);
	}

	public List<Station> getStations() {
		return this.sections.stream()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.sorted(Comparator.comparing(Station::getId))
			.collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Sections sections1 = (Sections)o;

		return sections.equals(sections1.sections);
	}

	@Override
	public int hashCode() {
		return sections.hashCode();
	}
}
