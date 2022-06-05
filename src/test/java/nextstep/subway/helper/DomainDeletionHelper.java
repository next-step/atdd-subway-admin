package nextstep.subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class DomainDeletionHelper {
    private DomainDeletionHelper() {
    }

    public static ExtractableResponse<Response> 지하철구간_삭제_요청(final Long lineId, final Long stationId) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .param("stationId", stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }
}
