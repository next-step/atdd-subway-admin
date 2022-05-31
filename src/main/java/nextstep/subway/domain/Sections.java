package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
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
		sections.sort(new Comparator<Section>() {
			@Override
			public int compare(Section s1, Section s2) {
				if (s1.getSectionOrder() > s2.getSectionOrder()) {
					return 1;
				}
				if (s1.getSectionOrder() < s2.getSectionOrder()) {
					return -1;
				}
				return 0;
			}
		});
		return sections;
	}
	
	public int lastIndex() {
		return this.sections.size() - 1;
	}
	
	public Section lastSection() {
		return sections.get(sections.size() - 1);
	}
	
	public void add(Section section) {
		validation(section);
		
		if(defaultAdd(section)) {
			return;
		}
		
		findAdd(section, findSectionIndex(section));
	}

	private void findAdd(Section section, OptionalInt findSectionIndex) {
		validationFindIndex(findSectionIndex);
		sections.get(findSectionIndex.getAsInt()).addNextSection(section);
		sectionsAdd(findSectionIndex.getAsInt(), section);
	}

	private boolean defaultAdd(Section section) {
		if(sections.isEmpty()) {
			sectionsAdd(section);
			return true;
		}

		if(isFirstSection(section)) {
			sectionsAdd(0, section);
			return true;
		}
		
		if(isLastSection(section)) {
			sectionsAdd(section);
			return true;
		}
		
		return false;
	}
	
	private void sectionsAdd(Section section) {
		this.sectionsAdd(sections.size(), section);
	}
	
	private void sectionsAdd(int index, Section section) {
		sections.add(section);
		section.addCompleted(index);
	}
	
	private void validationFindIndex(OptionalInt findSectionIndex) {
		if(!findSectionIndex.isPresent()) {
			throw new RuntimeException("인덱스를 찾지 못했습니다.");
		}
	}

	private void validation(Section section) {
		if(Objects.isNull(section.getId())) {
			throw new RuntimeException("역 정보를 찾지 못했습니다.");
		}
	}
	
	private OptionalInt findSectionIndex(Section section) {
		return IntStream
				.range(0, sections.size())
				.filter(index -> sections.get(index).isAddNextSection(section))
				.findFirst();
	}
	
	private boolean isFirstSection(Section section) {
		return sections.get(0).upStationEquals(section.getDownStation());
	} 
	
	private boolean isLastSection(Section section) {
		return lastSection().downStationEquals(section.getUpStation());
	}
}
