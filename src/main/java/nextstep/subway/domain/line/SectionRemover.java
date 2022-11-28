package nextstep.subway.domain.line;

import nextstep.subway.domain.strategy.FirstSectionRemoveStrategy;
import nextstep.subway.domain.strategy.LastSectionRemoveStrategy;
import nextstep.subway.domain.strategy.MiddleSectionRemoveStrategy;
import nextstep.subway.domain.strategy.SectionRemoveStrategy;

public class SectionRemover {

	private final SectionRemoveStrategy removeStrategy;

	private SectionRemover(Sections sections, Section sectionByUpStation, Section sectionByDownStation) {
		this.removeStrategy = strategy(sections, sectionByUpStation, sectionByDownStation);
	}

	public static SectionRemover of(Sections sections, Section sectionByUpStation, Section sectionByDownStation) {
		return new SectionRemover(sections, sectionByUpStation, sectionByDownStation);
	}

	public void remove() {
		removeStrategy.remove();
	}

	private SectionRemoveStrategy strategy(
		Sections sections,
		Section sectionByUpStation,
		Section sectionByDownStation
	) {
		if (isMiddleSection(sectionByUpStation, sectionByDownStation)) {
			return new MiddleSectionRemoveStrategy(sections, sectionByUpStation, sectionByDownStation);
		}
		if (isLastSection(sectionByUpStation, sectionByDownStation)) {
			return new LastSectionRemoveStrategy(sections, sectionByUpStation);
		}
		return new FirstSectionRemoveStrategy(sections, sectionByDownStation);
	}

	private boolean isMiddleSection(Section upSection, Section downSection) {
		return upSection != null && downSection != null;
	}

	private boolean isLastSection(Section upSection, Section downSection) {
		return upSection != null && downSection == null;
	}

}
