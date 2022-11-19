package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.station.acceptance.StationAcceptaneRequest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineAcceptance {
    public static LineDto.CreateRequest 라인_생성_요청_파라미터() {
        int upStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        int downStationId = 지하철역을_생성한다("양재역").jsonPath().get("id");
        String lineName = "신분당선";
        return new LineDto.CreateRequest()
                .setName(lineName)
                .setColor("bg-red-600")
                .setUpStationId(upStationId)
                .setDownStationId(downStationId)
                .setDistance(10);
    }

    public static void 지하철노선_존재(String name, Long upStationId, Long downStationId, int distance, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        params.put("color", color);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines/1")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선을_수정한다(LineDto.UpdateRequest line, int lineId) {
        return RestAssured.given()
                .body(line).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/"+lineId)
                .then().log().all()
                .extract();

    }

    public static ExtractableResponse<Response> 해당_노선을_제거한다(int stationId) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + stationId)
                .then().log().all()
                .extract();
    }

    public static void 생성한_노선을_찾을_수_있다(List<String> actual, String expect) {
        assertThat(actual).containsAnyOf(expect);
    }

    public static void 생성한_2개의_노선을_찾을_수_있다(List<String> actual) {
        assertThat(actual).hasSize(2);
    }

    public static void 생성한_지하철_노선의_정보를_응답받을_수_있다(ExtractableResponse<Response> response, LineDto.CreateRequest expect) {
        assertAll(
                () -> assertThat(response.jsonPath().get("id").toString()).isEqualTo("1"),
                () -> assertThat(response.jsonPath().get("name").toString()).isEqualTo(expect.getName()),
                ()-> assertThat(response.jsonPath().get("color").toString()).isEqualTo(expect.getColor())
        );
    }

    public static void 지하철_노선의_정보가_수정된다(ExtractableResponse<Response> response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선의_정보가_삭제된다(int lineId){
        ExtractableResponse<Response> response = 해당_노선을_제거한다(lineId);
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

}
