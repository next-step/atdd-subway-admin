package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;

public class SectionGroup {

	private static final int OUT_OF_INDEX = -1;
	private static final int ADJUST_NEXT_INDEX = 1;

	private List<Section> sections = new ArrayList<>();

	protected SectionGroup() {
	}

	public void add(Section section) {
		if (sections.isEmpty()) {
			sections.add(section);
			return;
		}
		addSectionWhenFindExistDownStation(section);
		addSectionWhenFindExistUpStation(section);
		adjustSectionsNoDuplicated();
	}

	private void adjustSectionsNoDuplicated() {
		sections.stream().distinct();
	}

	private void addSectionWhenFindExistUpStation(Section section) {
		int sectionIndexExistedUpStation = findSectionIndexExistedUpStation(section);
		if (OUT_OF_INDEX < sectionIndexExistedUpStation && !sections.contains(section)) {
			sections.add(sectionIndexExistedUpStation + ADJUST_NEXT_INDEX, section);
		}
	}

	private int findSectionIndexExistedUpStation(Section section) {
		return sections.stream()
			.filter(sectionInGroup -> sectionInGroup.downStation().equals(section.upStation()))
			.mapToInt(sections::indexOf)
			.findFirst()
			.orElse(OUT_OF_INDEX);
	}

	private void addSectionWhenFindExistDownStation(Section section) {
		int sectionIndexExistedDownStation = findSectionIndexExistedDownStation(section);
		if (OUT_OF_INDEX < sectionIndexExistedDownStation && !sections.contains(section)) {
			sections.add(sectionIndexExistedDownStation, section);
		}
	}

	private int findSectionIndexExistedDownStation(Section section) {
		return sections.stream()
			.filter(sectionInGroup -> sectionInGroup.upStation().equals(section.downStation()))
			.mapToInt(sections::indexOf)
			.findFirst()
			.orElse(OUT_OF_INDEX);
	}

	public List<Section> sections() {
		return sections;
	}
}