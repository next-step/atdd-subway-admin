package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;

public class SectionAcceptanceStep {
    private static final String LINE_BASE_PATH = "/lines";
    private static final String SECTION_BASE_PATH = "/sections";

    public static ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(LINE_BASE_PATH + "/" + lineId + SECTION_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_노선에_구간_등록에_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
