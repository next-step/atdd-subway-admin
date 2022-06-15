package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptacneTest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

public abstract class BaseSectionAcceptanceTest extends BaseAcceptacneTest {
    private static final String SECTION_URI = "/lines/{lineId}/sections";

    public static ExtractableResponse<Response> createSectionRequest(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(SECTION_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSectionRequest(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when().delete(SECTION_URI + "?stationId={stationId}", lineId, stationId)
                .then().log().all()
                .extract();
    }
}
