package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationRequestTestUtil;

public class LineRequestTestUtil {
	public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 종점_정보를_포함한_지하철_노선_생성_요청(String name, String color, String upStationId, String downStationId, String distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 종점역을_생성한_후_지하철_노선_생성_요청(String name, String color, String upStationName, String downStationName, String distance) {
		ExtractableResponse<Response> responseUpStation = StationRequestTestUtil.지하철역_생성(upStationName);
		ExtractableResponse<Response> responseDownStation = StationRequestTestUtil.지하철역_생성(downStationName);
		String upStationId = responseUpStation.jsonPath().getString("id");
		String downStationId = responseDownStation.jsonPath().getString("id");

		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 지하철_노선_조회_요청(String url) {
		return RestAssured.given().log().all()
			.when()
			.get(url)
			.then().log().all()
			.extract();
	}
}
