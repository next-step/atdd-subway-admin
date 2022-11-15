package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static nextstep.subway.line.LineAcceptanceStep.노선_한개_생성한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;

public class LineSectionStep {

    public static void 역_2개와_노선을_생성한다() {
        int upLastStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        int downLastStationId = 지하철역을_생성한다("선릉역").jsonPath().get("id");

        노선_한개_생성한다(upLastStationId, downLastStationId);
    }

    public static ExtractableResponse<Response> 역_사이에_새로운_역을_등록한다() {
        int newStationId =  지하철역을_생성한다("역삼역").jsonPath().get("id");

        return RestAssured.given()
                .body(params).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then()
                .log().all()
                .extract();
    }




}
