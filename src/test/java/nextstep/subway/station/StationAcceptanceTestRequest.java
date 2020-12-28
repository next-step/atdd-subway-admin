package nextstep.subway.station;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationAcceptanceTestRequest {

	public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		// when
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}

	public static Long 지하철역_등록되어_있음_등록된_ID(String name) {
		ExtractableResponse<Response> createResponse = 지하철역_생성_요청(name);
		return Long.parseLong(createResponse.header("Location").split("/")[2]);
	}
}
