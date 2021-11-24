package nextstep.subway.common;

import static nextstep.subway.common.ServiceApiFixture.*;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceFixture {

	public static long createLineId(String name, String color, Long upStationId, Long downStationId, int distance) {
		return postLines(lineAddRequest(name, color, upStationId, downStationId, distance))
			.as(LineResponse.class).getId();
	}

	public static long createStationId(String name) {
		return postStations(name).as(StationResponse.class).getId();
	}
}
