package nextstep.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TestSectionFactory {
    public static ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(LineResponse 노선, StationResponse 상행역
            , StationResponse 하행역, Integer 거리) {
        SectionRequest sectionRequest = SectionRequest.of(상행역.getId(), 하행역.getId(), 거리);
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/"+노선.getId()+"/sections")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    public static void 지하철_노선에_지하철역_구간_목록_포함됨(ExtractableResponse<Response> response, int expected) {
        LineResponse lineResponse = response.as(LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getStations()).hasSize(expected)
        );
    }

    public static void 지하철_노선에_지하철역_구간_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_삭제_요청(LineResponse 이호선, StationResponse 삭제역) {
        return RestAssured.given().log().all()
                .queryParam("stationId", 삭제역.getId())
                .when()
                .delete("/lines/"+이호선.getId()+"/sections")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_구간_삭제됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    public static void 지하철_노선_구간_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
