package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /***
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 노선_생성_요청_응답 = 노선_생성_요청(신분당선);
        노선_생성_성공_확인(노선_생성_요청_응답);
    }

    private ExtractableResponse<Response> 노선_생성_요청(LineRequest line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private void 노선_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> 노선_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }
}
