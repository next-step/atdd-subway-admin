package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.CommonMethodFixture;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceStep.SLASH;
import static org.assertj.core.api.Assertions.assertThat;


public class StationAcceptanceStep extends CommonMethodFixture {
    public static final String STATION_PATH = "/stations";


    public static void 지하철역_생성_호출(String name) {
        ExtractableResponse<Response> response = 지하철역을_생성한다(name);
        상태코드를_체크한다(response.statusCode(), HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        String path = STATION_PATH;
        return post(path, params);
    }

    public static void 상태코드를_체크한다(int statusCode, int value) {
        assertThat(statusCode).isEqualTo(value);
    }

    public static ExtractableResponse<Response> 모든_지하철역을_조회한다() {
        String path = STATION_PATH + SLASH;
        return get(path);
    }

    public static ExtractableResponse<Response> 특정_역_삭제(int id) {
        String path = STATION_PATH + SLASH + id;
        return delete(path);
    }

    public static ExtractableResponse<Response> 특정_역_조회(int id) {
        String path = STATION_PATH + SLASH + id;
        return get(path);
    }

    public static void 조회된_지하철역의_수가_일치한다(ExtractableResponse<Response> response, int size) {
        assertThat(response.jsonPath().getList("name", String.class)).hasSize(size);
    }

    public static void 지하철역_이름이_조회된다(List<String> allStationNames, String stationName) {
        assertThat(allStationNames).containsAnyOf(stationName);
    }




}
