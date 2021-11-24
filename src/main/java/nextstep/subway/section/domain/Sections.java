package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = { CascadeType.ALL }, orphanRemoval = true)
	List<Section> sections = new ArrayList<>();

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
		if (sections.size() == 0) {
			return result;
		}
		result.add(sections.get(0).getUpStation());
		result.addAll(sections.stream().map(Section::getDownStation).collect(Collectors.toList()));

		return result;
	}
}
