package nextstep.subway.line;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceStep.응답상태_검증;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceStep extends AcceptanceTest {
    public static ExtractableResponse<Response> 지하철노선에_구간_등록_요청(
            final Long lineId, final long upStationId, final Long downStationId, final int distance) {

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        //@formatter:off
          return RestAssured.given()
                                  .log().all()
                                  .body(params)
                                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .when()
                                  .post("/lines/" + lineId + "/sections")
                            .then()
                                  .log().all()
                            .extract();
          //@formatter:on
    }

    public static ExtractableResponse<Response> 지하철노선에_구간_삭제_요청(final Long lineId, final Long stationId) {

        //@formatter:off
          return RestAssured.given()
                                  .param("stationId", stationId)
                                  .log().all()
                            .when()
                                  .delete("/lines/" + lineId + "/sections")
                            .then()
                                  .log().all()
                            .extract();
          //@formatter:on
    }

    public static ExtractableResponse<Response> 지하철노선_구간_목록_조회_요청(Long lineId) {

        //@formatter:off
        return RestAssured.given()
                                .log().all()
                          .when()
                                .get("/lines/" + lineId + "/sections")
                          .then()
                                .log().all()
                          .extract();
        //@formatter:on
    }

    public static void 지하철구간_생성_응답상태_201_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철구간_생성_응답상태_400_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철구간_삭제_응답상태_204_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.NO_CONTENT);
    }

    public static void 지하철구간_삭제_응답상태_400_검증(ExtractableResponse<Response> response) {
        응답상태_검증(response, HttpStatus.BAD_REQUEST);
    }
}

