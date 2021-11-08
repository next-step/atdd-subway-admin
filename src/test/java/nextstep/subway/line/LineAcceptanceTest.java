package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
        // given
        String firstLineName = "1호선";
        String blueColor = "blue";

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineBody(firstLineName, blueColor))
            .contentType(ContentType.JSON)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        LineResponse lineResponse = response.as(LineResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank(),
            () -> assertThat(lineResponse.getId()).isNotNull(),
            () -> assertThat(lineResponse.getName()).isEqualTo(firstLineName),
            () -> assertThat(lineResponse.getColor()).isEqualTo(blueColor),
            () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
            () -> assertThat(lineResponse.getModifiedDate()).isNotNull()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_duplicateName_400() {
        // given
        Map<String, String> body = lineBody("1호선", "blue");
        createLine(body);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse firstLineResponse = createLine(lineBody("1호선", "blue")).as(LineResponse.class);
        LineResponse secondLineResponse = createLine(lineBody("2호선", "green")).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        //then
        List<LineResponse> lineResponses = response.as(new TypeRef<List<LineResponse>>() {
        });
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(lineResponses)
                .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
                .containsExactly(
                    tuple(firstLineResponse.getId(), firstLineResponse.getName(), firstLineResponse.getColor()),
                    tuple(secondLineResponse.getId(), secondLineResponse.getName(), secondLineResponse.getColor())
                )
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdFirstLineResponse = createLine(lineBody("1호선", "blue"));
        LineResponse firstLine = createdFirstLineResponse.as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(createdFirstLineResponse.header("Location"))
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.as(LineResponse.class))
                .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
                .containsExactly(firstLine.getId(), firstLine.getName(), firstLine.getColor())
        );
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_notExistsLine_404() {
        //given, when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", Long.MIN_VALUE)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String createdFirstLineUri = createLine(lineBody("1호선", "blue")).header("Location");
        String updatedSecondLineName = "2호선";
        String updatedRedColor = "red";

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(lineBody(updatedSecondLineName, updatedRedColor))
            .when()
            .put(createdFirstLineUri)
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(retrieveLine(createdFirstLineUri).as(LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsExactly(updatedSecondLineName, updatedRedColor)
        );
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateLine_notExistsLine_404() {
        //given, when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(lineBody("1호선", "blue"))
            .when()
            .put("/lines/{id}", Integer.MIN_VALUE)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String createdFirstLineUri = createLine(lineBody("1호선", "blue")).header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(createdFirstLineUri)
            .then().log().all()
            .extract();

        // then
        assertAll(
            () -> assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(retrieveLine(createdFirstLineUri).statusCode())
                .isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }

    private Map<String, String> lineBody(String name, String color) {
        HashMap<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        return body;
    }

    private ExtractableResponse<Response> createLine(Map<String, String> body) {
        return RestAssured.given()
            .body(body)
            .contentType(ContentType.JSON)
            .post("/lines")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> retrieveLine(String uri) {
        return RestAssured.get(uri)
            .then()
            .extract();
    }
}
