package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_구간_생성_요청(ExtractableResponse<Response> response, SectionRequest sectionRequest) {
        Integer lineId = response.jsonPath().get("id");
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_제거_요청(ExtractableResponse<Response> response, Long stationId) {
        Integer lineId = response.jsonPath().get("id");
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }
}
