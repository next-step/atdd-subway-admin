package nextstep.subway;

import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class AcceptanceTestUtil {
	public static void 응답_검증(ExtractableResponse<Response> response, HttpStatus status) {
		assertThat(response.statusCode()).isEqualTo(status.value());
	}
}
