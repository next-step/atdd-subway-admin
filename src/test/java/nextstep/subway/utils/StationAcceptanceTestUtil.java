package nextstep.subway.utils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class StationAcceptanceTestUtil {
    private static final String STATION_BASE_PATH = "/stations";
    private static final String NAME = "name";
    private static final String ID = "id";

    private StationAcceptanceTestUtil() {
    }

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put(NAME, stationName);

        return given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(STATION_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거(ExtractableResponse<Response> response) {
        return given().log().all()
            .pathParam(ID, response.jsonPath().getLong(ID))
            .when().delete(STATION_BASE_PATH +"/{id}")
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .extract();
    }

    public static List<String> 지하철_목록_조회() {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(STATION_BASE_PATH)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().jsonPath().getList(NAME, String.class);
    }

    public static void 지하철역_생성_성공_확인(ExtractableResponse<Response> statusCode) {
        assertThat(statusCode.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철역_생성_실패_확인(ExtractableResponse<Response> statusCode) {
        assertThat(statusCode.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_목록_검증_입력된_지하철역이_존재(List<String> actualNames, String... expectNames) {
        assertThat(actualNames).contains(expectNames);
    }

    public static void 지하철_목록_검증_입력된_지하철역이_존재하지_않음(List<String> returnStationNames, String... stationNames) {
        assertThat(returnStationNames).doesNotContain(stationNames);
    }
}
