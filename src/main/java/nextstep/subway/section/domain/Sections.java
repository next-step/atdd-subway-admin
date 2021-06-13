package nextstep.subway.section.domain;

import nextstep.subway.section.exception.NoSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Section getLongestSection() {
		return sections.stream().max(Comparator.comparingInt(Section::getDistance))
				.orElseThrow(NoSectionException::new);
	}

	public void addSection(Section... section) {
		sections.addAll(Arrays.asList(section));
	}
}
