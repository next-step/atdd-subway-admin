package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineTestCommon.createLineParams;
import static nextstep.subway.line.LineTestCommon.createResponse;


public class SectionAddAcceptanceTestRequest {
    public static ExtractableResponse<Response> 노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, long distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all().
                body(sectionRequest).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/"+lineId+"/sections").
                then().
                log().all().
                extract();
    }

    public static LineRequest 노선_구간_요청_생성(String lineName, String lineColor, Long upStationId, Long downStationId, long distance) {
        return createLineParams(lineName, lineColor, upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> 노선_등록시_구간_등록되어_있음(LineRequest request) {
        return createResponse(request, "/lines");
    }

    public static ExtractableResponse<Response> 지하철역_등록되어_있음(String name) {
        return 지하철역_생성_요청(name);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                extract();
    }
}
