package nextstep.subway.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationFactory {
	public static ExtractableResponse<Response> 지하철역_생성(String name) {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		return response;
	}
}
