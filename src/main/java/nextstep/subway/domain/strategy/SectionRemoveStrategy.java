package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public interface SectionRemoveStrategy {
	void remove(Sections sections, Section sectionByUpStation, Section sectionByDownStation);
}
