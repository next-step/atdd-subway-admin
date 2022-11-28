package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class LastSectionRemoveStrategy implements SectionRemoveStrategy {
	private final Sections sections;
	private final Section sectionByUpStation;

	public LastSectionRemoveStrategy(Sections sections, Section sectionByUpStation) {
		this.sections = sections;
		this.sectionByUpStation = sectionByUpStation;
	}

	@Override
	public void remove() {
		sections.removeSection(sectionByUpStation);
	}
}
