package nextstep.subway.station;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;

public class StationFixture {
	public static final Long UNKNOWN_STATION_ID = Long.MAX_VALUE;

	public static Station 강남역() {
		return Station.of(1L, "강남역");
	}

	public static Station 역삼역() {
		return Station.of(2L, "역삼역");
	}

	public static Station 선릉역() {
		return Station.of(3L, "선릉역");
	}

	public static Station 삼성역() {
		return Station.of(4L, "삼성역");
	}

	public static Station 사당역() {
		return Station.of(5L, "사당역");
	}

	public static Station 동작역() {
		return Station.of(6L, "동작역");
	}

	public static StationRequest 강남역_생성_요청값() {
		return new StationRequest("강남역");
	}

	public static StationRequest 역삼역_생성_요청값() {
		return new StationRequest("역삼역");
	}

	public static StationRequest 선릉역_생성_요청값() {
		return new StationRequest("선릉역");
	}

	public static StationRequest 삼성역_생성_요청값() {
		return new StationRequest("삼성역");
	}

	public static StationRequest 사당역_생성_요청값() {
		return new StationRequest("사당역");
	}

	public static StationRequest 동작역_생성_요청값() {
		return new StationRequest("동작역");
	}
}
