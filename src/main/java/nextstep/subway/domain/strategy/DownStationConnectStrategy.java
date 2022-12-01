package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.Sections;

public class DownStationConnectStrategy implements SectionConnectStrategy {

	private final Section existingSection;

	public DownStationConnectStrategy(Section existingSection) {
		this.existingSection = existingSection;
	}

	@Override
	public void connect(Sections sections, Section newSection) {
		rearrange(newSection);
		sections.add(newSection);
	}

	private void rearrange(Section newSection) {
		existingSection.replaceDownStation(newSection);
	}
}
