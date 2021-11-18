package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

public class LineSectionDeleteAcceptanceMethods extends AcceptanceTest {
    private static final String LINE_URL_PATH = "/lines/";
    private static final String SECTION_URL_PATH = "/sections";
    private static final String WITH_DELETE_STATION_ID_PARAM = "?stationId=";

    private LineSectionDeleteAcceptanceMethods() {}

    public static ExtractableResponse<Response> 지하철_노선의_지하철역_삭제_요청(Long lineId, Long stationId) {
        return delete(LINE_URL_PATH + lineId + SECTION_URL_PATH + WITH_DELETE_STATION_ID_PARAM + stationId);
    }

    public static void 지하철_노선에_지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선에_지하철역_삭제_실패(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
