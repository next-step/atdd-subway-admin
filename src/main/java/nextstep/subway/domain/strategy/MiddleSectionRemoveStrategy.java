package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class MiddleSectionRemoveStrategy implements SectionRemoveStrategy {

	private final Sections sections;
	private final Section sectionByUpStation;
	private final Section sectionByDownStation;

	public MiddleSectionRemoveStrategy(Sections sections, Section sectionByUpStation, Section sectionByDownStation) {
		this.sections = sections;
		this.sectionByUpStation = sectionByUpStation;
		this.sectionByDownStation = sectionByDownStation;
	}

	@Override
	public void remove() {
		sectionByDownStation.extend(sectionByUpStation);
		sections.removeSection(sectionByUpStation);
	}
}
