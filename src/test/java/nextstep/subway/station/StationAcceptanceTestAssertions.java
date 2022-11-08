package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationAcceptanceTestAssertions {

    static void 지하철역_생성_성공함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    static void 지하철역_생성_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    static void 지하철역_존재함(List<String> 지하철역_목록, String ...지하철역_이름) {
        assertThat(지하철역_목록).containsAnyOf(지하철역_이름);
    }

    static void 지하철역_존재하지_않음(List<String> 지하철역_목록, String ...지하철역_이름) {
        assertThat(지하철역_목록).doesNotContain(지하철역_이름);
    }
}
