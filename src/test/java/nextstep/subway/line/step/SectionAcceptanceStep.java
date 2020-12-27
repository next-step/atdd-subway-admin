package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceStep {
    public static ExtractableResponse<Response> 새로운_지하철_구간_추가_요청(
            final Long upStationId, final Long downStationId, final Long distance, final Long lineId
    ) {
        return RestAssured.given().log().all()
                .body(new SectionRequest(upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 새로운_지하철_하행종점역_구간_추가_요청(
            final Long originalEndDownStationId, final Long newEndDownStationId, final Long distance, final Long lineID
    ) {
        return 새로운_지하철_구간_추가_요청(originalEndDownStationId, newEndDownStationId, distance, lineID);
    }
}
