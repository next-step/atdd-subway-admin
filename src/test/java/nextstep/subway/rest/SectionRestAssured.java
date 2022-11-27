package nextstep.subway.rest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionCreateRequest;
import org.springframework.http.MediaType;

public class SectionRestAssured {

    public static ExtractableResponse<Response> 지하철_구간_추가(Long lineId, SectionCreateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RestResource.지하철_노선.uri() + "/" + lineId + RestResource.지하철_구간.uri())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_제거(Long lineId, Long stationId) {
        String url = RestResource.지하철_노선.uri() + "/{lineId}" + RestResource.지하철_구간.uri() + "?stationId={stationId}";
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(url, lineId, stationId)
                .then().log().all()
                .extract();
    }
}
