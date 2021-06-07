package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "경의선");
        params.put("color", "blue");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_생성됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");

        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "경의선");
        params.put("color", "blue");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("4호선", "green");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        Assertions.assertThat(lineResponses)
                .hasSize(2)
                .extracting("name").contains("경의선", "4호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");
        지하철_노선_등록되어_있음("4호선", "green");

        // when
        // 지하철_노선_조회_요청
        final String name = "경의선";
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{name}", name)
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        Assertions.assertThat(lineResponses)
                .hasSize(1)
                .extracting("name").contains("경의선");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");

        // when
        // 지하철_노선_수정_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "경의선");
        params.put("color", "red");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        Assertions.assertThat(lineResponses)
                .hasSize(1)
                .extracting("color").contains("red");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }

    void 지하철_노선_등록되어_있음(String name, String color) {
        Map<String, String> alreadyParams = new HashMap<>();
        alreadyParams.put("name", name);
        alreadyParams.put("color", color);

        ExtractableResponse<Response> alreadyResponse = RestAssured
                .given().log().all()
                .body(alreadyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        Assertions.assertThat(alreadyResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
