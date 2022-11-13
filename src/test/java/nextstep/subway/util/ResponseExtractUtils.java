package nextstep.subway.util;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class ResponseExtractUtils {

	private static final String ID = "id";

	private ResponseExtractUtils() {
		throw new AssertionError("Utility class cannot be instantiated");
	}

	public static Long id(ExtractableResponse<Response> response) {
		return response.response().jsonPath().getLong(ID);
	}

	public static String string(ExtractableResponse<Response> response, String property) {
		return response.response().jsonPath().getString(property);
	}
}
