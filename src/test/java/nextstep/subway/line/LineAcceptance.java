package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.springframework.http.MediaType;

public class LineAcceptance {

    /**
     * 전달받은 지하철역 목록을 저장한다
     *
     * @param name  노선 이름
     * @param color 노선 색상
     */
    public static ExtractableResponse<Response> 지하철_노선_생성(String name, String color,
        Long upStationId, Long downStationId, Long distance) {
        LineRequest lineRequest = LineRequest.of(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all().body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).when().post("/lines").then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(Long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static Long toLineId(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getId();
    }

    public static LineResponse toLine(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("", LineResponse.class);
    }

    public static String toLineName(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getName();
    }

    public static String toColorName(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getColor();
    }

    public static List<String> toLineNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    public static List<LineResponse> toList(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("", LineResponse.class);
    }

    public static List<String> toLineStationNames(LineResponse lineResponse) {
        return lineResponse.getStations().stream()
            .map(StationResponse::getName)
            .collect(Collectors.toList());
    }
}
