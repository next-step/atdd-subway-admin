package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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

		if (sections.isEmpty()) {
			sectionsAdd(section);
			return;
		}

		boolean findUpStation = sections.stream().anyMatch(s -> s.exists(section.getUpStation()));
		boolean findDownStation = sections.stream().anyMatch(s -> s.exists(section.getDownStation()));
		validationLink(findUpStation, findDownStation);

		if (findUpStation) {
			findUpStation(section);
		}

		if (findDownStation) {
			findDownStation(section);
		}
	}

	private void validationSectionIdNull(Section section) {
		if (Objects.isNull(section.getId())) {
			throw new RuntimeException("역 정보를 찾지 못했습니다.");
		}
	}

	private void validationLink(boolean findUpStation, boolean findDownStation) {
		if (findUpStation && findDownStation) {
			throw new RuntimeException("상행역과 하행역 모두 등록되어있습니다.");
		}

		if (!findUpStation && !findDownStation) {
			throw new RuntimeException("상행역과 하행역 모두 등록되어있지않습니다.");
		}
	}

	private boolean isFirstSection(Section section) {
		return sections.get(0).upStationEquals(section.getDownStation());
	}

	private boolean isLastSection(Section section) {
		return lastSection().downStationEquals(section.getUpStation());
	}

	private void findUpStation(Section section) {
		if (isLastSection(section)) {
			sectionsAdd(section);
			return;
		}
		int idx = IntStream.range(0, sections.size())
				.filter(index -> sections.get(index).upStationEquals(section.getUpStation())).findFirst().getAsInt();

		sectionIncreaseOrder(idx);
		sections.get(idx).addUpStation(section);
		sectionsAdd(idx, section);
	}

	private void findDownStation(Section section) {
		if (isFirstSection(section)) {
			sectionIncreaseOrder(0);
			sectionsAdd(0, section);
			return;
		}

		int idx = IntStream.range(0, sections.size())
				.filter(index -> sections.get(index).downStationEquals(section.getDownStation())).findFirst()
				.getAsInt();

		sectionIncreaseOrder(idx + 1);
		sections.get(idx).addDownStation(section);
		sectionsAdd(idx + 1, section);
	}

	private void sectionIncreaseOrder(int index) {
		sections.stream().filter(Section -> Section.getSectionOrder() > index).forEach(Section::orderIncrease);
	}

	private void sectionsAdd(Section section) {
		this.sectionsAdd(sections.size(), section);
	}

	private void sectionsAdd(int index, Section section) {
		sections.add(index, section);
		section.addCompleted(index);
	}

	@Override
	public String toString() {
		return "Sections [sections=" + sections + "]";
	}
}
