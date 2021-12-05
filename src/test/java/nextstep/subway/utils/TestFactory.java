package nextstep.subway.utils;

import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class TestFactory {

	public static ExtractableResponse<Response> create(String path, Map<String, String> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(path)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> findAll(String path) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(path)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> findById(String path) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(path)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> modify(String path, Map<String, String> params) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(params)
			.when()
			.put(path)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> delete(String path) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete(path)
			.then().log().all()
			.extract();
	}

}
