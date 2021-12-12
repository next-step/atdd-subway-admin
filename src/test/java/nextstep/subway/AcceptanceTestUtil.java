package nextstep.subway;

import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class AcceptanceTestUtil {
	public static final String HEADER_NAME_CREATED_RESOURCE_LOCATION = "Location";

	public static void 응답_검증(ExtractableResponse<Response> response, HttpStatus status) {
		assertThat(response.statusCode()).isEqualTo(status.value());
	}

	public static Long getLineIdFromCreatedLineResponse(ExtractableResponse<Response> response) {
		String[] createdLineResponseSplit = response.header(HEADER_NAME_CREATED_RESOURCE_LOCATION).trim().split("/");
		return Long.parseLong(createdLineResponseSplit[createdLineResponseSplit.length - 1]);
	}

	public static void 응답_메세지_검증(ExtractableResponse<Response> response, String message) {
		assertThat(response.asString()).isEqualTo(message);
	}
}
