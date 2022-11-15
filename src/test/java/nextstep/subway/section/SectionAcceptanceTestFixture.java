package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionAcceptanceTestFixture {

    public static ExtractableResponse<Response> 지하철_노선_구간_추가(Long id, Long upStationId,
        Long downStationId, int distance) {
        SectionRequest sectionRequest = SectionRequest.of(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", id)
            .then().log().all()
            .extract();
    }
}
