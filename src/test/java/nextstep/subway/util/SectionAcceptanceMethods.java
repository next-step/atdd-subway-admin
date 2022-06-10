package nextstep.subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceMethods {
    public static ExtractableResponse<Response> addSection(Long lineId, Long upStationId, Long downStationId,
                                                           Integer distance) {
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getSection(Long lineId, Long sectionId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId + "/" + sectionId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getAllSections(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
