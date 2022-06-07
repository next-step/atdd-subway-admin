package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public class AssertionsUtils {

    public static void assertCreated(Response response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void assertOk(Response response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void assertNoContent(Response response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
