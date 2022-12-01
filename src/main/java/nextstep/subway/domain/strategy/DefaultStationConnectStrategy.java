package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class DefaultStationConnectStrategy implements SectionConnectStrategy {

	@Override
	public void connect(Sections sections, Section newSection) {
		sections.add(newSection);
	}
}
