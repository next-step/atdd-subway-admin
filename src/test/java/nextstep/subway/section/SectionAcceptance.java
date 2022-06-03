package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptance {

    public static ExtractableResponse<Response> 지하철_구간_등록(Long 노선_id, SectionRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections" , 노선_id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_제거(Long 노선_id, Long 지하철역_id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("stationId", 지하철역_id)
            .when().delete("/lines/{lineId}/sections" , 노선_id)
            .then().log().all()
            .extract();
    }
}
