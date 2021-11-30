package nextstep.subway.line.dto;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.dto.SectionRequest;
import org.springframework.http.MediaType;

public class LineSectionAcceptanceTestRequest {

    public static ExtractableResponse<Response> addSectionInLine(String lineUri , Long preStationId, Long stationId, int distance) {
        SectionRequest sectionRequest = new SectionRequest(preStationId, stationId, new Distance(distance));
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(lineUri + "/sections")
                .then().log().all()
                .extract();
    }
}
