package nextstep.subway.domain.line;

import java.util.List;

import org.springframework.util.Assert;

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

	public void connect(Sections sections) {
		ConnectStrategyFactory.decide(newSection, existingSections)
			.connect(sections, newSection);
	}
}
