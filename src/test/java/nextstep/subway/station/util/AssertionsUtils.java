package nextstep.subway.station.util;

import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class AssertionsUtils {

	private AssertionsUtils() {
	}

	public static void assertStatusCode(final ExtractableResponse<Response> response, final HttpStatus status) {
		assertThat(response.statusCode()).isEqualTo(status.value());
	}
}
