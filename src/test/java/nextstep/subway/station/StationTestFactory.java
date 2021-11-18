package nextstep.subway.station;

import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.testFactory.AcceptanceTestFactory;

public class StationTestFactory {

	public static Map<String, String> 지하철역_이름_정의(String name) {
		return AcceptanceTestFactory.getNameContent(name);
	}

	public static ExtractableResponse<Response> 지하철역_생성(Map<String, String> params) {
		return AcceptanceTestFactory.post(params, StationAcceptanceTest.STATION_SERVICE_PATH);
	}
}
