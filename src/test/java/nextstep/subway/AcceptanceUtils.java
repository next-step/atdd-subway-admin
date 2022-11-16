package nextstep.subway;

import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptanceUtils {
    public static void assertStatusCode(ValidatableResponse response, HttpStatus httpStatus) {
        assertThat(response.extract().statusCode()).isEqualTo(httpStatus.value());
    }

    public static List<String> extractNames(ValidatableResponse response) {
        return response.extract().jsonPath().getList("name", String.class);
    }

    public static String extractName(ValidatableResponse response) {
        return response.extract().jsonPath().getString("name");
    }
}
