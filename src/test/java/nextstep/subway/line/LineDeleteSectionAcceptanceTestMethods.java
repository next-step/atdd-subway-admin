package nextstep.subway.line;

import static nextstep.subway.AcceptanceTest.delete;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public class LineDeleteSectionAcceptanceTestMethods {

    private static final String LINE_STATION_DELETE_PATH_FORMAT = "/lines/%s/sections?stationId=%s";

    public static ExtractableResponse<Response> 지하철_노선에_역_제거(Long lineId, Long stationId) {
        return delete(String.format(LINE_STATION_DELETE_PATH_FORMAT, lineId, stationId));
    }

    public static void 지하철_노선에_역_제거됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
