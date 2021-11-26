package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> createResponse = LineTestFixture.지하철_노선_등록("1호선", "blue");

        // then
        // 지하철_노선_생성됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = LineTestFixture.지하철_노선_등록("1호선", "blue");
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestFixture.지하철_노선_등록("1호선", "blue");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createBlueResponse = LineTestFixture.지하철_노선_등록("1호선", "blue");
        assertThat(createBlueResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> createGreenResponse = LineTestFixture.지하철_노선_등록("2호선", "green");
        assertThat(createGreenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("lines", Line.class)).hasSize(2);
    }

    @DisplayName("지하철 노선 조회 성공")
    @Test
    void getLineSuccess() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createBlueResponse = LineTestFixture.지하철_노선_등록("1호선", "blue");
        assertThat(createBlueResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> createGreenResponse = LineTestFixture.지하철_노선_등록("2호선", "green");
        assertThat(createGreenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_조회_요청
        Line line = createBlueResponse.as(Line.class);
        ExtractableResponse<Response> response = LineTestFixture.지하철_노선_조회(line.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", Line.class).getName()).isEqualTo(line.getName());
    }

    @DisplayName("지하철 노선 조회 실패")
    @Test
    void getLineFail() {

        ExtractableResponse<Response> response = LineTestFixture.지하철_노선_조회(1L);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createBlueResponse = LineTestFixture.지하철_노선_등록("1호선", "blue");
        assertThat(createBlueResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_수정_요청
        Line line = createBlueResponse.as(Line.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("color", "red");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", Line.class)).isEqualTo(line);
        assertThat(response.jsonPath().getObject(".", Line.class).getName()).isEqualTo("3호선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createBlueResponse = LineTestFixture.지하철_노선_등록("1호선", "blue");
        assertThat(createBlueResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_제거_요청
        Line line = createBlueResponse.as(Line.class);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> findResponse = LineTestFixture.지하철_노선_조회(line.getId());
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
