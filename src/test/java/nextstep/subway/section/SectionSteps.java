package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(SectionRequest params, long lineId) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_되어있음(SectionRequest params, long lineId) {
        return 지하철_노선에_지하철역_등록_요청(params, lineId);
    }

    public static ExtractableResponse<Response> 노선에서_지하철역_제거_요청(long lineId, long stationId) {
        return RestAssured
                .given().log().all()
                .queryParam("stationId", stationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all().extract();
    }
}
