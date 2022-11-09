package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LineAcceptanceTestAssertions {

    static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).startsWith("/lines/");
    }

    static void 지하철_노선_존재함(List<String> 지하철_노선_목록, String ...지하철_노선_이름) {
        assertThat(지하철_노선_목록).containsAnyOf(지하철_노선_이름);
    }

    static void 지하철_노선_존재함(String 생성된_지하철_노선_이름, String 요청한_지하철_노선_이름) {
        assertThat(생성된_지하철_노선_이름).isEqualTo(요청한_지하철_노선_이름);
    }

    public static void 지하철_노선_삭제됨(List<String> 지하철_노선_목록, String 지하철_노선) {
        assertThat(지하철_노선_목록).doesNotContain(지하철_노선);
    }
}
