package nextstep.subway.utils;

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

	public static ExtractableResponse<Response> put(String url, Object body) {
		return RestAssured
			.given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put(url)
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

}
