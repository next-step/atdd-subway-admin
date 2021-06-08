package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    public static final String BASE_PATH = "/sections";

    @DisplayName("구간등록 - 역 사이에 새로운 역(새로운 구간)을 등록하는 경우")
    @Test
    public void 역사이에새로운역을_등록하는경우_등록확인() throws Exception {
        //given
        LineResponse lineResponse = 지하철_노선_생성("5호선", "보라색", "방화역", "하남검단산역");
        LinesSubResponse linesSubResponse = 지하철_노선_조회();

        // when
        ExtractableResponse<Response> response = 구간등록(linesSubResponse);

        // then
        구간등록됨(response);
    }

    private ExtractableResponse<Response> 구간등록(LinesSubResponse linesSubResponse) {
        Map<String, String> params = 구간등록_파라미터(linesSubResponse);
        ExtractableResponse<Response> response = 구간등록_요청(params);
        return response;
    }

    private void 구간등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LinesSubResponse linesSubResponse = 지하철_노선_조회();
        assertThat(linesSubResponse.getStations().size()).isEqualTo(3);
    }

    private ExtractableResponse<Response> 구간등록_요청(Map<String, String> params) {
         return given()
                    .log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post(location + BASE_PATH)
                .then()
                    .log().all()
                    .extract();
    }

    private Map<String, String> 구간등록_파라미터(LinesSubResponse linesSubResponse) {
        Long upStationId = linesSubResponse.getStations().get(0).getId();
        Long downStationId = linesSubResponse.getStations().get(1).getId();
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", String.valueOf(downStationId));
        params.put("upStationId", String.valueOf(upStationId));
        params.put("distance", "10");
        return params;
    }
}
