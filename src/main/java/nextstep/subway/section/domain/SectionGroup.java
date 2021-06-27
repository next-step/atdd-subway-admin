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

import nextstep.subway.exception.CannotAddNewSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class SectionGroup {

	private static final int OUT_OF_INDEX = -1;
	private static final int FIRST_INDEX = 0;
	private static final int NEXT_INDEX = 1;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "section_id", foreignKey = @ForeignKey(name = "fk_line_section"))
	private List<Section> sections = new ArrayList<>();

	public SectionGroup() {
	}

	public SectionGroup(Section... sections) {
		Arrays.stream(sections).forEach(this::add);
	}

	public List<Section> sections() {
		return sections;
	}

	public List<Station> stations() {
		return sections.stream()
			.flatMap(section -> Arrays.stream(new Station[] {section.upStation(), section.downStation()}))
			.distinct()
			.collect(Collectors.toList());
	}

	public void add(Section targetSection) {
		if (sections.isEmpty()) {
			sections.add(targetSection);
			return;
		}
		validateSection(targetSection);
		addTargetSection(targetSection);
	}

	private void addTargetSection(Section targetSection) {
		addTargetSectionWhenSameDownStation(targetSection);
		addTargetSectionWhenSameUpStation(targetSection);
		addTargetSectionWhenEdgeStation(targetSection);
	}

	private void validateSection(Section targetSection) {
		boolean isExistUpStation = stations().contains(targetSection.upStation());
		boolean isExistDownStation = stations().contains(targetSection.downStation());
		if (isExistUpStation && isExistDownStation) {
			throw new CannotAddNewSectionException("해당 노선에 상행역과 하행역이 등록되어있는 상태입니다.");
		}
		if (!isExistUpStation && !isExistDownStation) {
			throw new CannotAddNewSectionException("상행역과 하행역 중 하나도 해당 노선에 등록되어있지 않습니다.");
		}
	}

	private void addTargetSectionWhenSameDownStation(Section targetSection) {
		int sourceSectionIndex = targetSection.findSectionIndexWhenSameDownStation(this);
		if (OUT_OF_INDEX < sourceSectionIndex) {
			Section sourceSection = sections.get(sourceSectionIndex);
			sourceSection.updateWhenSameDownStation(targetSection);
			sourceSection.minusDistance(targetSection.distance());
			sections.add(sourceSectionIndex + NEXT_INDEX, targetSection);
		}
	}

	private void addTargetSectionWhenSameUpStation(Section targetSection) {
		int sourceSectionIndex = targetSection.findSectionIndexWhenSameUpStation(this);
		if (OUT_OF_INDEX < sourceSectionIndex) {
			Section sourceSection = sections.get(sourceSectionIndex);
			sourceSection.updateWhenSameUpStation(targetSection);
			sourceSection.minusDistance(targetSection.distance());
			sections.add(sourceSectionIndex, targetSection);
		}
	}

	private void addTargetSectionWhenEdgeStation(Section targetSection) {
		if (targetSection.isLastSection(this)) {
			sections.add(targetSection);
			return;
		}
		if (targetSection.isFirstSection(this)) {
			sections.add(FIRST_INDEX, targetSection);
			return;
		}
	}

	public void sort() {
		this.sections = new SectionGroup(sections.toArray(new Section[sections.size()])).sections;
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
