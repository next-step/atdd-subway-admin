package nextstep.subway.line;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTestRequest;

public class LineAcceptanceTestRequest {

	public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
		LineRequest request = createLineRequest(name, color, upStationId, downStationId, distance);

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
		return response;
	}

	public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, String upStationName, String downStationName, int distance) {
		LineRequest request = createLineRequest(name, color, upStationName, downStationName, distance);

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
		return response;
	}

	public static String 지하철_노선_등록되어_있음(String name, String color, Long upStationId, Long downStationId, int distance) {
		ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(name, color, upStationId, downStationId, distance);
		return createResponse.header("Location");
	}

	public static String 지하철_노선_등록되어_있음(String name, String color, String upStationName, String downStationName, int distance) {
		ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(name, color, upStationName, downStationName, distance);
		return createResponse.header("Location");
	}

	public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();
		return response;
	}

	public static ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get(uri)
			.then().log().all().extract();
		return response;
	}

	public static ExtractableResponse<Response> 지하철_노선_수정_요청(String uri, String name, String color) {
		LineRequest request = createLineRequest(name, color);

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put(uri)
			.then().log().all().extract();
		return response;
	}

	public static ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete(uri)
			.then().log().all().extract();
		return response;
	}

	private static LineRequest createLineRequest (String name, String color) {
		return new LineRequest(name, color);
	}

	private static LineRequest createLineRequest (String name, String color, Long upStationId, Long downStationId, int distance) {
		return new LineRequest(name, color, upStationId, downStationId, distance);
	}

	private static LineRequest createLineRequest (String name, String color, String upStationName, String downStationName, int distance) {
		Long upStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID(upStationName);
		Long downStationId = StationAcceptanceTestRequest.지하철역_등록되어_있음_등록된_ID(downStationName);
		return new LineRequest(name, color, upStationId, downStationId, distance);
	}
}
