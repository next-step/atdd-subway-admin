package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class LastSectionRemoveStrategy implements SectionRemoveStrategy {
	@Override
	public void remove(Sections sections, Section sectionByUpStation, Section sectionByDownStation) {
		sections.removeSection(sectionByUpStation);
	}
}
