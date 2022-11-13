package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Isolationer;
import nextstep.subway.dto.LineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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

    public static ExtractableResponse<Response> 지하철노선_수정_요청_성공(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().patch("/lines/1")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_삭제_요청_성공(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().delete("/lines/1")
                .then().log().all()
                .extract();
    }

}
