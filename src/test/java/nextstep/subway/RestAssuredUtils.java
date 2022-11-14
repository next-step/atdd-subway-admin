package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

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
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract();
	}

	public static ExtractableResponse<Response> getAll(String requestPath) {
		return get(requestPath);
	}

	public static List<String> getAll(String requestPath, String fieldName) {
		return get(requestPath).jsonPath().getList(fieldName, String.class);
	}

	public static String get(String requestPath, long id, String fieldName) {
		return get(requestPath + "/" + id).jsonPath().getString(fieldName);
	}

	public static ExtractableResponse<Response> get(String requestPath, long id) {
		return get(requestPath + "/" + id);
	}

	public static ExtractableResponse<Response> delete(String requestPath, Long id) {
		return RestAssured.given().log().all()
			.when().delete(requestPath + "/" + id)
			.then().log().all().statusCode(HttpStatus.NO_CONTENT.value())
			.extract();
	}

	private static ExtractableResponse<Response> get(String requestPath) {
		return RestAssured.given().log().all()
			.when().get(requestPath)
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract();
	}
}
