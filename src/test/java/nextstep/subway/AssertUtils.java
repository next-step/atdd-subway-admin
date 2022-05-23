package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public final class AssertUtils {

    private AssertUtils() {
    }

    public static void assertStatusCode(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    public static long toId(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getLong("id");
    }
}
