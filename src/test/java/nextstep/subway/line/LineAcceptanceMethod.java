package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

public class LineAcceptanceMethod extends AcceptanceTest {

	public LineResponse 지하철_2호선_생성요청() {
		return 지하철_노선_생성_요청(createLine2Params()).as(LineResponse.class);
	}

	public void 지하철_구간_추가(LineResponse line, String stationId, String distance) {
		지하철_노선_구간추가_요청(line.getId(),
			createSectionParam(line.getStations().get(0).getId().toString(), (stationId), distance));
	}

	public ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}

	public ExtractableResponse<Response> 지하철_노선_전체_조회_요청() {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines")
			.then().log().all().extract();
	}

	public ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines/" + id)
			.then().log().all().extract();
	}

	public ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Map<String, String> params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/" + id)
			.then().log().all()
			.extract();
	}

	public ExtractableResponse<Response> 지하철_노선_삭제_요청(Long id) {
		return RestAssured
			.given().log().all()
			.when().delete("/lines/" + id)
			.then().log().all()
			.extract();
	}

	public ExtractableResponse<Response> 지하철역_생성_요청(String station) {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", station);

		// when
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}

	public ExtractableResponse<Response> 지하철_노선_구간추가_요청(long lineId, Map<String, String> params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines/" + lineId + "/sections")
			.then().log().all().extract();
	}

	public ExtractableResponse<Response> 지하철_노선_구간삭제_요청(long id, String stationId) {
		return RestAssured
			.given().log().all()
			.when().delete("/lines/" + id + "/sections?stationId=" + stationId)
			.then().log().all().extract();
	}

	public Map<String, String> createLine1Params() {
		return generateParam("1호선", "blue", "인천역", "소요산역", "350");
	}

	public Map<String, String> createLine2Params() {
		return generateParam("2호선", "green", "시청역", "서초역", "100");
	}

	public Map<String, String> generateParam(String name, String color, String upStationName, String downStationName,
		String distance) {

		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", createStationId(upStationName));
		params.put("downStationId", createStationId(downStationName));
		params.put("distance", distance);
		return params;
	}

	public Map<String, String> createSectionParam(String upStationId, String downStationId, String distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);

		return params;
	}

	public String createStationId(String name) {
		ExtractableResponse<Response> response = 지하철역_생성_요청(name);
		return response.as(StationResponse.class).getId().toString();
	}
}
