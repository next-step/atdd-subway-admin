package nextstep.subway.station;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationSteps {
	public static ExtractableResponse<Response> request_station_creation(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/stations")
			.then().log().all()
			.extract();
	}
}
