package nextstep.subway.domain;

import java.util.ArrayList;
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
		
		OptionalInt findSectionIndex = findSectionIndex(section);
		validationFindIndex(findSectionIndex);
		sections.get(findSectionIndex.getAsInt()).addNextSection(section);
		sections.add(findSectionIndex.getAsInt(), section);
	}

	private boolean defaultAdd(Section section) {
		if(sections.isEmpty()) {
			sections.add(section);
			return true;
		}

		if(isFirstSection(section)) {
			sections.add(0, section);
			return true;
		}
		
		if(isLastSection(section)) {
			sections.add(section);
			return true;
		}
		
		return false;
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
