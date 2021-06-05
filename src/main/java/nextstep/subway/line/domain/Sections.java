package nextstep.subway.line.domain;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

	@OneToMany(fetch = LAZY, mappedBy = "line", cascade = ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	boolean isIn(Line line) {
		return sections.stream()
			.allMatch(section -> section.isIn(line));
	}

	void add(Section section) {
		this.sections.add(section);
	}
}
