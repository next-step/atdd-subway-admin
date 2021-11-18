package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sectionList = new ArrayList<>();

	protected Sections() {
	}

	public Sections(final List<Section> sectionList) {
		this.sectionList = Collections.unmodifiableList(sectionList);
	}

	public List<Section> getSectionList() {
		return sectionList;
	}

	public void add(Section section) {
		sectionList.add(section);
	}
}
