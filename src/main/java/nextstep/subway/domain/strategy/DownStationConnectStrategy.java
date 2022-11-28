package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;

public class DownStationConnectStrategy implements SectionConnectStrategy {

	private final Section existingSection;

	public DownStationConnectStrategy(Section existingSection) {
		this.existingSection = existingSection;
	}

	@Override
	public void connect(Section newSection) {
		existingSection.replaceDownStation(newSection);
	}
}
