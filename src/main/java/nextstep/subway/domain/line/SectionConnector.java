package nextstep.subway.domain.line;

import java.util.List;
import java.util.Optional;

import org.springframework.util.Assert;

import nextstep.subway.domain.strategy.DownStationConnectStrategy;
import nextstep.subway.domain.strategy.UpStationConnectStrategy;

public class SectionConnector {


	private final Section newSection;
	private final List<Section> existingSections;

	private SectionConnector(Section newSection, List<Section> existingSections) {
		Assert.notNull(newSection, "newSection must not be null");
		Assert.notNull(existingSections, "existingSections must not be null");
		this.newSection = newSection;
		this.existingSections = existingSections;
	}

	public static SectionConnector of(Section newSection, List<Section> existingSections) {
		return new SectionConnector(newSection, existingSections);
	}

	public void connect() {
		if (existingSections.isEmpty()) {
			return;
		}
		Optional<Section> sameUpStationSection = sameUpStationSection(newSection, existingSections);
		if (sameUpStationSection.isPresent()) {
			new UpStationConnectStrategy(sameUpStationSection.get()).connect(newSection);
			return;
		}
		Optional<Section> sameDownStationSection = sameDownStationSection(newSection, existingSections);
		sameDownStationSection.ifPresent(section -> new DownStationConnectStrategy(section).connect(newSection));
	}

	private Optional<Section> sameDownStationSection(Section newSection, List<Section> existingSections) {
		return existingSections.stream()
			.filter(it -> it.isSameDownStation(newSection))
			.findFirst();
	}

	private Optional<Section> sameUpStationSection(Section newSection, List<Section> existingSections) {
		return existingSections.stream()
			.filter(section -> section.isSameUpStation(newSection))
			.findFirst();
	}

}
