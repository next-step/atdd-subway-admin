package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.section.dto.SectionRequest;

public class LineSectionAddAcceptanceMethods extends AcceptanceTest {
    private static final String LINE_URL_PATH = "/lines/";
    private static final String SECTION_URL_PATH = "/sections";

    public LineSectionAddAcceptanceMethods() {}

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return post(LINE_URL_PATH + lineId + SECTION_URL_PATH, sectionRequest);
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_구간_등록_실패(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
