package nextstep.subway.line;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceStep.등록된_지하철역;
import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceStep extends AcceptanceTest {
    public static ExtractableResponse<Response> 등록된_지하철노선(String name, String color, int distance, final String stationId1, final String stationId2) {
        StationResponse station1 = 등록된_지하철역(stationId1).as(StationResponse.class);
        StationResponse station2 = 등록된_지하철역(stationId2).as(StationResponse.class);
        return 지하철노선_생성_요청(name, color, station1.getId(), station2.getId(), distance);
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color,
                                                            Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        //@formatter:off
        return RestAssured.given()
                                .log().all()
                                .body(params)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                                .post("/lines")
                          .then()
                                .log().all()
                          .extract();
        //@formatter:on
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회_요청() {

        //@formatter:off
        return RestAssured.given()
                                .log().all()
                          .when()
                                .get("/lines")
                          .then()
                                .log().all()
                          .extract();
        //@formatter:on
    }

    public static ExtractableResponse<Response> 지하철노선_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        //@formatter:off
        return RestAssured.given()
                                .log().all()
                          .when()
                                .get(uri)
                          .then()
                                .log().all()
                          .extract();
        //@formatter:on
    }

    public static ExtractableResponse<Response> 지하철노선_수정_요청(
            ExtractableResponse<Response> response, String name, String color) {

        String uri = response.header("Location");
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        //@formatter:off
        return RestAssured.given()
                                .log().all()
                                .body(params)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                                .put(uri)
                          .then()
                                .log().all()
                          .extract();
        //@formatter:on
    }

    public static ExtractableResponse<Response> 지하철노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        //@formatter:off
        return RestAssured.given()
                                .log().all()
                          .when()
                                .delete(uri)
                          .then()
                                .log().all()
                          .extract();
        //@formatter:on
    }

    public static void 지하철노선_생성_응답상태_201_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철노선_생성_응답상태_400_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철노선_목록_조회_응답상태_200_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.OK);
    }

    public static void 지하철노선_조회_응답상태_200_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.OK);
    }

    public static void 지하철노선_수정_응답상태_200_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.OK);
    }

    public static void 지하철_노선_삭제_응답상태_204_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.NO_CONTENT);
    }

    static void 응답상태_검증(final ExtractableResponse<Response> response, final HttpStatus ok) {
        assertThat(response.statusCode()).isEqualTo(ok.value());
    }

    public static void 지하철노선_목록_검증(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}

