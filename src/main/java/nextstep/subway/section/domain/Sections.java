package nextstep.subway.section.domain;

import static java.util.stream.Collectors.*;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

/**
 *
 * @author heetaek.kim
 */
@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private final List<Section> sections = new LinkedList<>();

	public Sections(Section section) {
		if (section == null) {
			throw new IllegalArgumentException("section is null");
		}
		this.sections.add(section);
	}

	public List<Station> orderedStations() {
		return sections.stream()
			.flatMap(Section::stations)
			.distinct()
			.collect(toList());
	}

	public void addSection(Section section) {
		validateAddable(section);
		sections.add(section);
	}

	private void validateAddable(Section section) {
		// TODO validateAddable
		// 1. 상행/하행이 동일한 섹션이 있는 경우.
		// 2. 상행/하행이 포함된 섹션이 모두 없는 경우.
		// 3. 거리 확인.
	}
}
