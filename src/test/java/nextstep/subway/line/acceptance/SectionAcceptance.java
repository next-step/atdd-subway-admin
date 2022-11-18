package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

public class SectionAcceptance {
    public static ExtractableResponse<Response> update_section(Long lineId, Long upStationId,
            Long downStationId, int distance) {
        return RestAssured.given().log().all()
                .body(new SectionRequest(upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static List<SectionResponse> section_list_was_queried(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList(".", SectionResponse.class);
    }

    public static ExtractableResponse<Response> delete_section(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }
}
