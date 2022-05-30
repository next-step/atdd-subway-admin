package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany
	@JoinColumn(name = "section_id")
	private List<Section> sections;
	
	public Sections() {
		sections = new ArrayList<>();
	}
	
	public List<Section> getSections() {
		return sections;
	}
	
	public int lastIndex() {
		return this.sections.size() - 1;
	}
	
	public void add(Section section) {
		sections.add(section);
	}
}
