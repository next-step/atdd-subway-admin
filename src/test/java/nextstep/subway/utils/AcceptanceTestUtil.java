package nextstep.subway.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class AcceptanceTestUtil {

	private AcceptanceTestUtil() {
	}

	public static ExtractableResponse<Response> get(String url) {
		return RestAssured
			.given().log().all()
			.when()
			.get(url)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> get(String url, Map<String, Object> pathParams) {
		return RestAssured
			.given().log().all()
			.pathParams(pathParams)
			.when()
			.get(url)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> post(String url, Object body) {
		return RestAssured
			.given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(url)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> put(String url, Object body, Map<String, Object> pathParams) {
		return RestAssured
			.given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.pathParams(pathParams)
			.when()
			.put(url)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> delete(String url, Map<String, Object> pathParams) {
		return RestAssured
			.given().log().all()
			.pathParams(pathParams)
			.when()
			.delete(url)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> delete(String url) {
		return RestAssured
			.given().log().all()
			.when()
			.delete(url)
			.then().log().all()
			.extract();
	}

	public static Map<String, Object> pathParamsForId(Long lineId) {
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("id", lineId);
		return pathParams;
	}
}
