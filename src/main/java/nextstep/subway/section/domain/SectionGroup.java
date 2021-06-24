package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class SectionGroup {

	private static final int OUT_OF_INDEX = -1;
	private static final int ADJUST_NEXT_INDEX = 1;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "section_id", foreignKey = @ForeignKey(name = "fk_line_section"))
	private List<Section> sections = new ArrayList<>();

	public SectionGroup() {
	}

	public SectionGroup(Section... sections) {
		this.sections = Arrays.stream(sections).collect(Collectors.toList());
	}

	public void add(Section section) {
		if (sections.isEmpty()) {
			sections.add(section);
			return;
		}
		addSectionWhenFoundDownStation(section);
		addSectionWhenFoundUpStation(section);
		adjustSectionsNoDuplicated();
	}

	private void adjustSectionsNoDuplicated() {
		sections.stream().distinct();
	}

	private void addSectionWhenFoundUpStation(Section targetSection) {
		int sectionIndex = targetSection.findSectionIndexWithinUpStations(this);
		if (OUT_OF_INDEX < sectionIndex && !sections.contains(targetSection)) {
			sections.add(sectionIndex, targetSection);
		}
	}

	private void addSectionWhenFoundDownStation(Section targetSection) {
		int sectionIndex = targetSection.findSectionIndexWithinDownStations(this);
		if (OUT_OF_INDEX < sectionIndex && !sections.contains(targetSection)) {
			sections.add(sectionIndex + ADJUST_NEXT_INDEX, targetSection);
		}
	}

	public List<Section> sections() {
		return sections;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof SectionGroup)) {
			return false;
		}
		SectionGroup that = (SectionGroup)object;
		return sections.containsAll(this.sections)
			&& that.sections.containsAll(sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}
}
