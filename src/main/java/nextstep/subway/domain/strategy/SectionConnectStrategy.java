package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public interface SectionConnectStrategy {
	void connect(Sections sections, Section section);
}
