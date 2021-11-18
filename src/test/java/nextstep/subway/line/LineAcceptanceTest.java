package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");
        final Long id = response.jsonPath().getObject(".", LineResponse.class).getId();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + id);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("red");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_요청("신분당선", "red");
        지하철_노선_생성_요청("1호선", "indigo");

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/lines")
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(".", LineResponse.class)).hasSize(2);
        assertThat(response.jsonPath().getList("name", String.class)).contains("신분당선", "1호선");
        assertThat(response.jsonPath().getList("color", String.class)).contains("red", "indigo");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final ExtractableResponse<Response> given = 지하철_노선_생성_요청("신분당선", "red");
        final Long id = given.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/lines/" + id)
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("red");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final ExtractableResponse<Response> given = 지하철_노선_생성_요청("신분당선", "red");
        final Long id = given.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        final Map<String, String> params = new HashMap<>();
        params.put("name", "구분당선");
        params.put("color", "blue");

        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final ExtractableResponse<Response> given = 지하철_노선_생성_요청("신분당선", "red");
        final Long id = given.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        final ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().delete("/lines/" + id)
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color) {
        final HashMap<Object, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
            .given().log().all()
            .body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }
}
