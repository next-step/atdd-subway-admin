package nextstep.subway.line.accecptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceResponse {
    public static void 지하철노선_생성_성공(ExtractableResponse<Response> response) {
        String lineName = response.jsonPath().get("name");
        assertThat(lineName).isEqualTo("2호선");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철노선_조회_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(".")).hasSize(2);
    }
}
