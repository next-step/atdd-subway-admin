package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceStep {
    public static ExtractableResponse<Response> REQUEST_SECTION_CREATE(
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
}
