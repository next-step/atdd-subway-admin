package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.MediaType;

import static nextstep.subway.line.step.LineAcceptanceStep.EXTRACT_ID_FROM_RESPONSE_LOCATION;
import static nextstep.subway.station.step.StationAcceptanceStep.CREATED_STATION;

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

    public static ExtractableResponse<Response> NEW_END_DOWN_SECTION_TO_LINE(
            final Long originalEndDownStationId, final Long newEndDownStationId, final Long distance, final Long lineID
    ) {
        return REQUEST_SECTION_CREATE(originalEndDownStationId, newEndDownStationId, distance, lineID);
    }
}
