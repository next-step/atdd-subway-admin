package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceCommon {

    public static ExtractableResponse<Response> 지하철_구간_등록(long id, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제(long lineId, long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}/sections?stationId={stationId}",lineId,stationId)
                .then().log().all()
                .extract();
    }
}
