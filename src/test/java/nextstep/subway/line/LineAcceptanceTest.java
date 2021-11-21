package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철_노선을_생성한다() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("신분당선", "red");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 지하철_노선_목록을_조회한다() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("신분당선", "red");
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("2호선", "green");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(response.jsonPath().getList(".")).hasSize(2);
        assertThat(response.jsonPath().getString("name")).contains("신분당선");
        assertThat(response.jsonPath().getString("name")).contains("2호선");
    }

    @Test
    void 지하철_노선을_조회한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_조회_요청

        // then
        // 지하철_노선_응답됨
    }

    @Test
    void 지하철_노선을_수정한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @Test
    void 지하철_노선을_제거한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }
}
