package nextstep.subway;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RestAssuredUtils {

	public static ExtractableResponse<Response> post(String requestPath, Map<String, String> body) {
		return RestAssured.given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post(requestPath)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> put(String requestPath, long id, Map<String, String> body) {
		return RestAssured.given().log().all()
			.body(body)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put(requestPath + "/" + id)
			.then().log().all()
			.extract();
	}

	public static List<String> getAll(String requestPath, String fieldName) {
		return get(requestPath).jsonPath().getList(fieldName, String.class);
	}

	public static String get(String requestPath, long id, String fieldName) {
		return get(requestPath + "/" + id).jsonPath().getString(fieldName);
	}

	public static ExtractableResponse<Response> delete(String requestPath, Long id) {
		return RestAssured.given().log().all()
			.when().delete(requestPath + "/" + id)
			.then().log().all()
			.extract();
	}

	private static ExtractableResponse<Response> get(String requestPath) {
		return RestAssured.given().log().all()
			.when().get(requestPath)
			.then().log().all()
			.extract();
	}
}
