package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.testFactory.AcceptanceTestFactory;

public class LineTestFactory {

	public static ExtractableResponse<Response> 지하철_노선_생성(Map<String, String> params) {
		return AcceptanceTestFactory.post(params, LineAcceptanceTest.LINE_SERVICE_PATH);
	}

	public static Map<String, String> 지하철_노선_정보_정의(String name, String color, Long upStationId, Long downStationId,
		int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));
		return params;
	}
}
