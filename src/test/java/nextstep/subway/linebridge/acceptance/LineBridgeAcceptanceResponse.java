package nextstep.subway.linebridge.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class LineBridgeAcceptanceResponse {
    public static void 지하철구간_생성_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철구간_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }
}
