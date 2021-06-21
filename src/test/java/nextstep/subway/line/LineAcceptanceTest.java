package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * {@link nextstep.subway.line.ui.LineController#createLine(LineRequest)}
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        String lineName = "line name";
        String lineColor = "line color";
        LineRequest params = new LineRequest(lineName, lineColor);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().as(Line.class))
            .hasFieldOrPropertyWithValue("name", lineName)
            .hasFieldOrPropertyWithValue("color", lineColor);
        assertThat(response.header("Location"))
            .isNotBlank()
            .startsWith("/lines/");
    }

    /**
     * {@link nextstep.subway.line.ui.LineController#createLine(LineRequest)}
     */
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성시 실패한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "line name";
        String lineColor = "line color";
        LineRequest params = new LineRequest(lineName, lineColor);
        postLine(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * {@link nextstep.subway.line.ui.LineController#createLine(LineRequest)}
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "line name";
        String lineColor = "line color";
        postLine(new LineRequest(lineName, lineColor));
        // 지하철_노선_등록되어_있음
        String lineName2 = "line name2";
        String lineColor2 = "line color2";
        postLine(new LineRequest(lineName2, lineColor2));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> lines = response.body().as(new TypeRef<List<LineResponse>>() {});
        assertThat(lines)
            .hasSize(2)
            .extracting(LineResponse::getName, LineResponse::getColor)
            .containsExactlyInAnyOrder(
                tuple(lineName, lineColor),
                tuple(lineName2, lineColor2)
            );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "line name";
        String lineColor = "line color";
        LineResponse lineResponse = postLine(new LineRequest(lineName, lineColor));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/" + lineResponse.getId())
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse result = response.body().as(LineResponse.class);
        assertThat(result)
            .hasFieldOrPropertyWithValue("id", lineResponse.getId())
            .hasFieldOrPropertyWithValue("name", lineName)
            .hasFieldOrPropertyWithValue("color", lineColor);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
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

    @SuppressWarnings("UnusedReturnValue")
    LineResponse postLine(LineRequest params) {
        return RestAssured.given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().body().as(LineResponse.class);
    }
}
