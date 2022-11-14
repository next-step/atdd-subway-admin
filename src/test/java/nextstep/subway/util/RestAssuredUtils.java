package nextstep.subway.util;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtils {

	private static final RequestSpecification requestSpecification;

	private RestAssuredUtils() {
		throw new AssertionError("Utility class cannot be instantiated");
	}

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

	public static <T> ValidatableResponse post(final String url, final T request) {
		return requestSpecification
			.body(request)
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

	public static <T> ValidatableResponse put(final String url, final T updateRequest) {
		return requestSpecification
			.body(updateRequest)
			.when()
			.put(url)
			.then().log().all();
	}
}
