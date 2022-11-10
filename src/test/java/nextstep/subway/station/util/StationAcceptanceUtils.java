package nextstep.subway.station.util;

import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationAcceptanceUtils {

	private static final String STATION_URL = "/stations";

	private StationAcceptanceUtils() {
		throw new AssertionError("Utility class cannot be instantiated");
	}

	public static ExtractableResponse<Response> 지하철역_생성_요청(final String name) {
		Map<String, String> param = RequestParamUtils.generateRequestParam("name", name);
		return RestAssuredUtils.post(STATION_URL, param).extract();
	}

	 public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
		 return RestAssuredUtils.get(STATION_URL).extract();
	 }

	 public static ExtractableResponse<Response> 지하철역_삭제_요청(final String id) {
		 return RestAssuredUtils.delete(STATION_URL + "/" + id).extract();
	 }
}
