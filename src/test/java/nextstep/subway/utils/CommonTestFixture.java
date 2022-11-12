package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.HttpStatus;

public class CommonTestFixture {
    public static final String STATION_BASE_PATH = "/stations";
    public static final String LINE_BASE_PATH = "/lines";
    public static final String PATH_VARIABLE_ID = "/{id}";
    public static final String ID = "id";
    public static final String NAME = "name";

    public static long 응답_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong(ID);
    }

    public static void HTTP_상태코드_검증(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

    public static void 목록_검증_존재함(List<String> actualNames, String[] expectNames) {
        assertThat(actualNames).contains(expectNames);
    }

    public static void 목록_검증_존재하지_않음(List<String> actualNames, String[] stationNames) {
        assertThat(actualNames).doesNotContain(stationNames);
    }
}
