package nextstep.subway.station.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
			.when().post("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_목록_조회_요청() {
		return RestAssured.given().log().all()
			.when().get("/lines")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_조회_요청(Long lineId) {
		return RestAssured.given().log().all()
			.when().get("/lines/{longId}", lineId)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_조회_요청(ExtractableResponse<Response> response) {
		String uri = response.header("Location");

		return RestAssured.given().log().all()
			.when().get(uri)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_수정_요청(ExtractableResponse<Response> response, Map<String, String> params) {
		String uri = response.header("Location");

		return RestAssured.given().log().all()
			.body(params)
			.contentType(ContentType.JSON)
			.when().put(uri)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_삭제_요청(ExtractableResponse<Response> response) {
		String uri = response.header("Location");

		return RestAssured.given().log().all()
			.when().delete(uri)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 구간_생성_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));

		return RestAssured.given().log().all()
			.body(params)
			.contentType(ContentType.JSON)
			.when().post("/lines/{lineId}/sections", lineId)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 구간_삭제_요청(Long lindId, Long stationId) {
		return RestAssured.given().log().all()
			.when().delete("/lines/{lineId}/sections?stationId={stationId}", lindId, stationId)
			.then().log().all()
			.extract();
	}
}
