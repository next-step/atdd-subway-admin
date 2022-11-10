package nextstep.subway.station.util;

import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtils {

	private static final RequestSpecification requestSpecification;

	static {
		requestSpecification = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE);
	}

	public static ValidatableResponse get(final String url) {
		return requestSpecification
			.when()
			.get(url)
			.then().log().all();
	}

	public static ValidatableResponse post(final String url, final Map<String, String> requestParam) {
		return requestSpecification
			.body(requestParam)
			.when()
			.post(url)
			.then().log().all();
	}

	public static ValidatableResponse delete(final String url) {
		return requestSpecification
			.when()
			.delete(url)
			.then().log().all();
	}
}
