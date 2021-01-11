package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "line_id")
	private List<Section> sections = new ArrayList<>();

	public List<Section> getSections() {
		return sections;
	}

	public void add(Section section) {
		sections.stream()
			.filter(s -> s.getUpStation().getId() == section.getUpStation().getId())
			.findFirst()
			.ifPresent(s -> s.updateUpStation(s.getUpStation()));
		sections.add(section);
	}
}
