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

    public static ExtractableResponse<Response> 지하철_노선의_구간_목록_조회됨(long lineId) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{lineId}/sections", lineId)
                .then().log().all().extract();
    }
}
