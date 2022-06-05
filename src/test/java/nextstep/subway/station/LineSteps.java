package nextstep.subway.station;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LineSteps {
	public static ExtractableResponse<Response> 노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.post("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_목록_조회_요청() {
		return RestAssured.given().log().all()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_조회_요청(ExtractableResponse<Response> response) {
		String uri = response.header("Location");

		return RestAssured.given().log().all()
			.get(uri)
			.then().log().all()
			.extract();
	}
}
