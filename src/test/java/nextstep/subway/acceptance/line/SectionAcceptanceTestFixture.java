package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTestFixture {

    public static final String BASE_URL = "/lines/{lineId}/sections";

    public static ExtractableResponse<Response> 구간_생성(Long lineId, SectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_URL, lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 구간_삭제(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete(BASE_URL + "?stationId={stationId}", lineId, stationId)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_구간이_추가된다(ExtractableResponse<Response> 지하철_노선_조회) {
        List<StationResponse> stations = 지하철_노선_조회.jsonPath().getList("stations");
        assertThat(stations).hasSize(3);
    }

    public static void 구간_생성에_실패한다(int statusCode) {
        assertThat(statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 구간_삭제에_실패한다(int statusCode) {
        assertThat(statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
