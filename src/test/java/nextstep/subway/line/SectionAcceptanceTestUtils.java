package nextstep.subway.line;

import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceTestUtils {

	private SectionAcceptanceTestUtils() {
		// empty
	}

	static SectionRequest createSectionRequest(final StationResponse upStation, final StationResponse downStation, final int distance) {
		return new SectionRequest(upStation.getId(), downStation.getId(), distance);
	}
}
