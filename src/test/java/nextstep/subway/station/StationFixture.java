package nextstep.subway.station;

import nextstep.subway.station.dto.StationRequest;

public class StationFixture {
	public static final Long UNKNOWN_STATION_ID = Long.MAX_VALUE;

	public static StationRequest 강남역_생성_요청값() {
		return new StationRequest("강남역");
	}

	public static StationRequest 역삼역_생성_요청값() {
		return new StationRequest("역삼역");
	}
}
