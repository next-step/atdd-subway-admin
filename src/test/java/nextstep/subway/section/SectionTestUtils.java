package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTestUtils {
    public static final String PATH = "/lines";

    public static ExtractableResponse<Response> 구간_추가_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PATH + "/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 구간_삭제_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete(PATH + "/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static void 구간_추가_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 구간_추가_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 구간_삭제_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 구간_삭제_찾기_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static void 구간_삭제_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
