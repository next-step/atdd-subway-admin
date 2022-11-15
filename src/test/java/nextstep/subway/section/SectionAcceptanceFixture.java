package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceFixture {

    public static ExtractableResponse<Response> 지하철_구간_생성_요청(Long lineId, SectionRequest 지하철_구간_요청) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .body(지하철_구간_요청)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
