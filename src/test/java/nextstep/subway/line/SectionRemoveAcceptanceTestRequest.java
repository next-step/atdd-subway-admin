package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTestRequest.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineAcceptanceTestRequest.지하철_노선_요청_생성;


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
