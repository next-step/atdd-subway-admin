package nextstep.subway.comm;

import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CustomExtractableResponse {
	public static ExtractableResponse<Response> get(String url) {
		return RestAssured
				.given()
					.log().all()
				.when()
					.get(url)
				.then()
					.log().all()
				.extract();
	}
	
	public static ExtractableResponse<Response> post(String url, Map<String, String> params) {
		return RestAssured
				.given()
				    .body(params)
				    .contentType(MediaType.APPLICATION_JSON_VALUE)
				    .log().all()
				.when()
					.post(url)
				.then()
					.log().all()
				.extract();
	}
    
	public static ExtractableResponse<Response> delete(String url) {
		return RestAssured
				.given()
					.log().all()
				.when()
					.delete(url)
				.then()
					.log().all()
				.extract();
	}
	
	public static <T> String joinUrl(String url, T value) {
		return url + "/" + value.toString();
	}
}
