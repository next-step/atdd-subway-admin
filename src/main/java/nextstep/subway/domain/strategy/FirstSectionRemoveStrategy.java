package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class FirstSectionRemoveStrategy implements SectionRemoveStrategy {

	@Override
	public void remove(Sections sections, Section sectionByUpStation, Section sectionByDownStation) {
		sections.removeSection(sectionByDownStation);
	}
}
