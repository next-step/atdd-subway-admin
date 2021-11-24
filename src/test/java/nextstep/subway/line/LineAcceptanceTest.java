package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        final LineRequest createRequest = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(createRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        final LineRequest createRequest = new LineRequest("신분당선", "bg-red-600");
        RestAssured.given().log().all()
            .body(createRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(createRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        final LineRequest createRequest1 = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(createRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // 지하철_노선_등록되어_있음
        final LineRequest createRequest2 = new LineRequest("2호선", "bg-green-600");
        final ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(createRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        final List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        final List<Long> actualLineIds = response.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        final LineRequest createRequest = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(createRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        final Long createdLineId = createResponse.jsonPath()
            .getObject(".", LineResponse.class)
            .getId();

        // when
        // 지하철_노선_조회_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + createdLineId.toString())
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_조회됨
        final Long actualLineId = response.jsonPath()
            .getObject(".", LineResponse.class)
            .getId();
        assertThat(actualLineId).isEqualTo(createdLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        final LineRequest createRequest = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(createRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        final Long createdLineId = createResponse.jsonPath()
            .getObject(".", LineResponse.class)
            .getId();

        // when
        // 지하철_노선_수정_요청
        final LineRequest updateRequest = new LineRequest("구분당선", "bg-blue-600");
        final ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
            .body(updateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines" + "/" + createdLineId)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_수정됨
        final LineResponse actualLine = RestAssured.given().log().all()
            .when()
            .get("/lines/" + createdLineId.toString())
            .then().log().all()
            .extract()
            .jsonPath()
            .getObject(".", LineResponse.class);
        assertAll(
            () -> assertThat(actualLine.getId()).isEqualTo(createdLineId),
            () -> assertThat(actualLine.getName()).isEqualTo(updateRequest.getName()),
            () -> assertThat(actualLine.getColor()).isEqualTo(updateRequest.getColor())
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        final LineRequest createRequest = new LineRequest("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(createRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        // 지하철_노선_제거_요청
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        // 지하철_노선_삭제됨_2
        final ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
