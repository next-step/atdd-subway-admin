package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String firstLineName = "1호선";
        String blueColor = "blue";

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(firstLineName, blueColor);

        // then
        지하철_노선_생성됨(response, firstLineName, blueColor);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_duplicateName_400() {
        // given
        String firstLineName = "1호선";
        String blueColor = "blue";
        지하철_노선_등록되어_있음(firstLineName, blueColor);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(firstLineName, blueColor);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse firstLineResponse = 지하철_노선_등록되어_있음("1호선", "blue").as(LineResponse.class);
        LineResponse secondLineResponse = 지하철_노선_등록되어_있음("2호선", "green").as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        //then
        assertAll(
            () -> 지하철_노선_목록_응답됨(response),
            () -> 지하철_노선_목록_포함됨(response, Arrays.asList(firstLineResponse, secondLineResponse))
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdFirstLineResponse = 지하철_노선_등록되어_있음("1호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdFirstLineResponse);

        // then
        지하철_노선_응답됨(response, createdFirstLineResponse.as(LineResponse.class));
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_notExistsLine_404() {
        //given, when
        ExtractableResponse<Response> response = 존재하지_않는_지하철_노선_조회_요청();

        // then
        지하철_노선_못찾음(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음("1호선", "blue");
        String updatedSecondLineName = "2호선";
        String updatedRedColor = "red";

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdResponse,
            updatedSecondLineName, updatedRedColor);

        // then
        지하철_노선_수정됨(response, createdResponse, updatedSecondLineName, updatedRedColor);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateLine_notExistsLine_404() {
        //when
        ExtractableResponse<Response> response = 존재하지_않는_지하철_노선_수정_요청();

        // then
        지하철_노선_못찾음(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음("1호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdResponse);

        // then
        지하철_노선_삭제됨(response, createdResponse);
    }

    private String headerLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineResponse expectedLine) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.as(LineResponse.class))
                .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
                .containsExactly(expectedLine.getId(), expectedLine.getName(), expectedLine.getColor())
        );
    }

    private void 지하철_노선_못찾음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철_노선_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", Long.MIN_VALUE)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, ExtractableResponse<Response> createdResponse,
        String expectedName, String expectedColor) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(지하철_노선_조회_요청(createdResponse).as(LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsExactly(expectedName, expectedColor)
        );
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createdResponse,
        String name, String color) {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(lineBody(name, color))
            .when()
            .put(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철_노선_수정_요청() {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(lineBody("1호선", "blue"))
            .when()
            .put("/lines/{id}", Integer.MIN_VALUE)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createdResponse) {
        return RestAssured.given().log().all()
            .when()
            .get(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private AbstractIntegerAssert<?> 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode())
            .isEqualTo(HttpStatus.OK.value());
    }

    private AbstractListAssert<?, List<? extends Tuple>, Tuple, ObjectAssert<Tuple>> 지하철_노선_목록_포함됨(
        ExtractableResponse<Response> response, List<LineResponse> expectedLines) {
        List<LineResponse> lineResponses = response.as(new TypeRef<List<LineResponse>>() {
        });
        return assertThat(lineResponses)
            .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
            .containsExactly(
                expectedLines.stream()
                    .map(line -> tuple(line.getId(), line.getName(), line.getColor()))
                    .toArray(Tuple[]::new)
            );
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response, String expectedName, String expectedColor) {
        LineResponse lineResponse = response.as(LineResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(headerLocation(response)).isNotBlank(),
            () -> assertThat(lineResponse.getId()).isNotNull(),
            () -> assertThat(lineResponse.getName()).isEqualTo(expectedName),
            () -> assertThat(lineResponse.getColor()).isEqualTo(expectedColor),
            () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
            () -> assertThat(lineResponse.getModifiedDate()).isNotNull()
        );
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        return RestAssured.given().log().all()
            .body(lineBody(name, color))
            .contentType(ContentType.JSON)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        return RestAssured.given()
            .body(lineBody(name, color))
            .contentType(ContentType.JSON)
            .post("/lines")
            .then()
            .extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response, ExtractableResponse<Response> createdResponse) {
        assertAll(
            () -> assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(지하철_노선_조회_요청(createdResponse).statusCode())
                .isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createdResponse) {
        return RestAssured.given().log().all()
            .when()
            .delete(headerLocation(createdResponse))
            .then().log().all()
            .extract();
    }

    private Map<String, String> lineBody(String name, String color) {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        return body;
    }
}
