package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.StationNotFoundException;

@Embeddable
public class Sections {
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name = "section_id")
	private List<Section> sections;

	public Sections() {
		sections = new ArrayList<>();
	}

	public List<Section> getSections() {
		sections.sort(Comparator.comparingInt(Section::getSectionOrder));
		return sections;
	}

	private int lastIndex() {
		return this.sections.size() - 1;
	}

	public Section lastSection() {
		return sections.get(lastIndex());
	}

	public void add(Section section) {
		validationSectionIdNull(section);

		if (getSections().isEmpty()) {
			addSections(section);
			return;
		}

		boolean isFindUpStation = sections.stream().anyMatch(s -> s.exists(section.getUpStation()));
		boolean isFindDownStation = sections.stream().anyMatch(s -> s.exists(section.getDownStation()));
		validationLink(isFindUpStation, isFindDownStation);

		if (isFindUpStation) {
			findUpStation(section);
		}

		if (isFindDownStation) {
			findDownStation(section);
		}
	}

	private void validationSectionIdNull(Section section) {
		if (Objects.isNull(section.getId())) {
			throw new RuntimeException("역 정보를 찾지 못했습니다.");
		}
	}

	private void validationLink(boolean isFindUpStation, boolean isFindDownStation) {
		if (isFindUpStation && isFindDownStation) {
			throw new RuntimeException("상행역과 하행역 모두 등록되어있습니다.");
		}

		if (!isFindUpStation && !isFindDownStation) {
			throw new RuntimeException("상행역과 하행역 모두 등록되어있지않습니다.");
		}
	}

	private boolean isFirstSection(Section section) {
		return isFirstSection(section.getDownStation());
	}
	
	private boolean isFirstSection(Station station) {
		return sections.get(0).upStationEquals(station);
	}

	private boolean isLastSection(Section section) {
		return isLastSection(section.getUpStation());
	}

	private boolean isLastSection(Station station) {
		return lastSection().downStationEquals(station);
	}

	private void findUpStation(Section section) {
		if (isLastSection(section)) {
			addSections(section);
			return;
		}
		int idx = IntStream.range(0, sections.size())
				.filter(index -> sections.get(index).upStationEquals(section.getUpStation())).findFirst().getAsInt();

		sectionIncreaseOrder(idx);
		sections.get(idx).addUpStation(section);
		addSections(idx, section);
	}

	private void findDownStation(Section section) {
		if (isFirstSection(section)) {
			sectionIncreaseOrder(0);
			addSections(0, section);
			return;
		}

		int idx = IntStream.range(0, sections.size())
				.filter(index -> sections.get(index).downStationEquals(section.getDownStation())).findFirst()
				.getAsInt();

		sectionIncreaseOrder(idx + 1);
		sections.get(idx).addDownStation(section);
		addSections(idx + 1, section);
	}

	private void sectionIncreaseOrder(int index) {
		sections.stream().filter(Section -> Section.getSectionOrder() >= index).forEach(Section::orderIncrease);
	}

	private void sectionDecreaseOrder(int index) {
		sections.stream().filter(Section -> Section.getSectionOrder() >= index).forEach(Section::orderDecrease);
	}

	private void addSections(Section section) {
		this.addSections(sections.size(), section);
	}

	private void addSections(int index, Section section) {
		sections.add(index, section);
		section.updateSectionOrder(index);
	}

	public void removeSection(Station removeStation) {
		validationRemoveSection(removeStation);

		if(isFirstSection(removeStation)) {
			sections.remove(0);
			sectionDecreaseOrder(0);
			return;
		}
		
		if(isLastSection(removeStation)) {
			sections.remove(lastIndex());
			return;
		}
		
		removeSection(removeSectionsIndex(removeStation));
	}

	private void removeSection(int idx) {
		Section beforeSection = sections.get(idx - 1); 
		beforeSection.removeAfterSection(sections.get(idx));
		sections.remove(idx);
		sectionDecreaseOrder(idx);
	}

	private int removeSectionsIndex(Station removeStation) {
		return IntStream.range(0, sections.size())
				.filter(index -> sections.get(index).upStationEquals(removeStation))
				.findFirst()
				.orElseThrow(() -> new StationNotFoundException());
	}

	private void validationRemoveSection(Station removeStation) {
		if(getSections().size() < 2) {
			throw new IllegalArgumentException("구간이 2개이상 등록된 경우에만 삭제가 가능합니다.");
		}
	}

	@Override
	public String toString() {
		return "Sections [sections=" + sections + "]";
	}
}
