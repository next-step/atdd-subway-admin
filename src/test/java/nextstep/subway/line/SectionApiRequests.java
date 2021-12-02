package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.utils.CommonTestApiClient;

public class SectionApiRequests {
    public static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        String uri = "/lines/" + lineId + "/sections";
        return CommonTestApiClient.post(sectionRequest, uri);
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제_요청(Long lineId, Long stationId) {
        String uri = "/lines/" + lineId + "/sections";
        return RestAssured
                .given().log().all()
                .param("stationId", stationId)
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
