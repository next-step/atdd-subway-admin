package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AssertUtils {

    public static void assertBadRequestAndMessage(ExtractableResponse<Response> response, String errorMessage) {
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.jsonPath().getString("message")).isEqualTo(errorMessage);
        });
    }

    public static void assertHttpStatusOk(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
