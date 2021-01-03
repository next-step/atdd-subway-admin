package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class SectionRemoveAcceptanceTestRequest {
    public static ExtractableResponse<Response> 노선에_지하철역_제거_요청(Long lineId, Long removeStationId) {
        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                delete("/lines/{lineId}/sections?stationId={stationId}", lineId, removeStationId).
                then().
                log().all().
                extract();
    }
}
