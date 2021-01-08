package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceTestRequest {
    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(long lineId, long upStationId, long downStationId, int distance) {
        SectionRequest request = createSectionRequest(upStationId, downStationId, distance);
        return 지하철_노선_구간_등록_요청(lineId, request);
    }

    private static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(long lineId, SectionRequest request) {
        return RestAssured
                .given().log().all()
                .pathParam("lineId", lineId)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections")
                .then().log().all().extract();
    }

    private static SectionRequest createSectionRequest(long upStationId, long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }
}
