package nextstep.subway.testFactory;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AcceptanceTestFactory {

	public static Map<String, String> getNameContent(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return params;
	}

	public static void 정상_생성_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	public static void 예외_발생_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	public static void 정상_처리_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 삭제_완료_확인(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	public static ExtractableResponse<Response> post(Map<String, String> params, String path) {
		return postBase(preBase(params).post(path));
	}

	public static ExtractableResponse<Response> get(String path) {
		return postBase(preBase().get(path));
	}

	public static ExtractableResponse<Response> put(Map<String, String> params, String path) {
		return postBase(preBase(params).put(path));
	}

	public static ExtractableResponse<Response> delete(String path) {
		return postBase(preBase().delete(path));
	}

	private static RequestSpecification preBase(Map<String, String> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when();
	}

	private static RequestSpecification preBase() {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when();
	}

	private static ExtractableResponse<Response> postBase(Response response) {
		return response
			.then().log().all()
			.extract();
	}

	public static Map<String, String> getLineInfo(String name, String color, Long upStationId, Long downStationId,
		int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", String.valueOf(upStationId));
		params.put("downStationId", String.valueOf(downStationId));
		params.put("distance", String.valueOf(distance));
		return params;
	}
}

