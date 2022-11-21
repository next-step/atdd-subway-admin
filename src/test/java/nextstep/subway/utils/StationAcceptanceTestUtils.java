package nextstep.subway.utils;

import static nextstep.subway.utils.CommonTestFixture.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class StationAcceptanceTestUtils {
    public static final String 잠실역 = "잠실역";
    public static final String 몽촌토성역 = "몽촌토성역";

    private StationAcceptanceTestUtils() {
    }

    public static ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME, stationName);

        return 생성(STATION_BASE_PATH, params);
    }

    public static List<String> 지하철_목록을_조회한다() {
        return 목록_조회(STATION_BASE_PATH);
    }

    public static ExtractableResponse<Response> 지하철역을_제거한다(ExtractableResponse<Response> response) {
        return 삭제(STATION_BASE_PATH + PATH_VARIABLE_ID, 응답_ID_추출(response));
    }

    public static void 지하철역_생성을_확인한다(ExtractableResponse<Response> response) {
        HTTP_상태코드_검증(response.statusCode(), HttpStatus.CREATED);
    }

    public static void 지하철역_생성_실패를_확인한다(ExtractableResponse<Response> response) {
        HTTP_상태코드_검증(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    public static void 지하철_목록_검증_입력된_지하철역이_존재(List<String> actualNames, String... expectNames) {
        목록_검증_존재함(actualNames, expectNames);
    }

    public static void 지하철_목록_검증_입력된_지하철역이_존재하지_않음(List<String> actualNames, String... stationNames) {
        목록_검증_존재하지_않음(actualNames, stationNames);
    }
}
