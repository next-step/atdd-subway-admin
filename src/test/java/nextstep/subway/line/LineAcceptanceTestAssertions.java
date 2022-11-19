package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTestAssertions {

    static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).startsWith("/lines/");
    }

    static void 지하철_노선_존재함(List<String> 지하철_노선_목록, String ...지하철_노선_이름) {
        assertThat(지하철_노선_목록).containsAnyOf(지하철_노선_이름);
    }

    public static void 지하철_노선_존재함(String 생성된_지하철_노선_이름, String 요청한_지하철_노선_이름) {
        assertThat(생성된_지하철_노선_이름).isEqualTo(요청한_지하철_노선_이름);
    }

    public static void 지하철_노선_삭제됨(List<String> 지하철_노선_목록, String 지하철_노선) {
        assertThat(지하철_노선_목록).doesNotContain(지하철_노선);
    }

    public static void 역_목록_일치함(ExtractableResponse<Response> 노선_조회_응답, List<String> 역_목록) {
        LineResponse 본문 = 노선_조회_응답.body().as(LineResponse.class);
        assertThat(본문.getStations())
                .extracting(StationResponse::getName)
                .containsExactlyElementsOf(역_목록);
    }
}
