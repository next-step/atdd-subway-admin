package nextstep.subway.domain.strategy;

import nextstep.subway.domain.line.Section;

public class UpStationConnectStrategy implements SectionConnectStrategy {

	private final Section existingSection;

	public UpStationConnectStrategy(Section existingSection) {
		this.existingSection = existingSection;
	}

	@Override
	public void connect(Section newSection) {
		existingSection.replaceUpStation(newSection);
	}
}
