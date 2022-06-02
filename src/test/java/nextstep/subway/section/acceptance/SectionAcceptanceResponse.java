package nextstep.subway.section.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceResponse {
    public static void 지하철구간_생성_성공(ExtractableResponse<Response> response) {
        Integer distance = response.jsonPath().get("distance");
        assertThat(distance).isEqualTo(10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
