package nextstep.subway.domain.line;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new LinkedList<>();

	protected Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public static Sections initialSections(Section section) {
		List<Section> sections = new LinkedList<>();
		sections.add(section);
		return new Sections(sections);
	}

	public List<Section> getSections() {
		return sections;
	}
}
