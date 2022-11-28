package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class FirstSectionRemoveStrategy implements SectionRemoveStrategy {

	private final Sections sections;
	private final Section sectionByDownStation;

	public FirstSectionRemoveStrategy(Sections sections, Section sectionByDownStation) {
		this.sections = sections;
		this.sectionByDownStation = sectionByDownStation;
	}

	@Override
	public void remove() {
		sections.removeSection(sectionByDownStation);
	}
}
