package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionTestFixture {
    public static ExtractableResponse<Response> requestAddSection(Long upStationId, Long downStationId, int distance){
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/1/sections")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }
}
