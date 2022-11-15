package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class LineAcceptanceStep {

    public static void 해당_노선의_정보가_삭제된다(int id) {
        ExtractableResponse<Response> response = 특정_노선을_조회한다(id);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    public static void 지하철_노선_정보_수정_확인(int id, ExtractableResponse<Response> result) {
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> savedLine = 특정_노선을_조회한다(id);

        assertAll(
                ()-> assertThat(savedLine.jsonPath().getString("name")).isEqualTo("새로운 노선"),
                ()-> assertThat(savedLine.jsonPath().getString("color")).isEqualTo("파란색"),
                ()-> assertThat(savedLine.jsonPath().getInt("distance")).isEqualTo(33)
        );
    }

    public static ExtractableResponse<Response> 노선_정보를_수정한다(int id) {
        LineRequest updateRequest = LineRequest.builder()
                .name("새로운 노선")
                .color("파란색")
                .distance(33)
                .build();

        return RestAssured.given()
                .body(updateRequest).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/line/" + id)
                .then()
                .log().all()
                .extract();
    }


    public static ExtractableResponse<Response> 노선_한개_생성한다(int upLastStationId, int downLastStationId) {
        LineRequest request = LineRequest.builder()
                .name("2호선")
                .color("green darken-2")
                .distance(7)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();

        return 노선을_생성한다(request);
    }
    public static ExtractableResponse<Response> 노선을_생성한다(LineRequest request) {
        return RestAssured.given()
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static List<String> 모든_노선_이름을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    public static void 노선_이름이_조회된다(List<String> allLineNames, String lineName) {
        assertThat(allLineNames).containsAnyOf(lineName);
    }

    public static void 노선_2개_생성(int upLastStationId, int downLastStationId) {
        LineRequest request1 = LineRequest.builder()
                .name("2호선")
                .color("red darken-2")
                .distance(100)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();
        노선을_생성한다(request1);

        LineRequest request2 = LineRequest.builder()
                .name("분당선")
                .color("yellow darken-1")
                .distance(20)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();
        노선을_생성한다(request2);
    }


    public static ExtractableResponse<Response> 모든_노선을_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static void 노선의_수가_일치한다(ExtractableResponse<Response> allLines, int size) {
        assertThat(allLines.jsonPath().getList("name", String.class)).hasSize(size);
    }

    public static ExtractableResponse<Response> 특정_노선을_조회한다(int id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/line/" + id)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_정보_확인(ExtractableResponse<Response> savedLine, ExtractableResponse<Response> result) {
        assertAll(
                () -> assertThat(result.jsonPath().getInt("id")).isEqualTo(savedLine.jsonPath().getInt("id")),
                () -> assertThat(result.jsonPath().getString("name")).isEqualTo(savedLine.jsonPath().getString("name")),
                ()-> assertThat(result.jsonPath().getString("color")).isEqualTo(savedLine.jsonPath().getString("color")),
                ()-> assertThat(result.jsonPath().getInt("distance")).isEqualTo(savedLine.jsonPath().getInt("distance"))
        );
    }

    public static ExtractableResponse<Response> 특정_노선을_제거한다(int id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/line/" + id)
                .then().log().all()
                .extract();
    }



}
