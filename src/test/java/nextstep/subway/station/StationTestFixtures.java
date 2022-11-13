package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.fixtures.TestFixtures;

class StationTestFixtures extends TestFixtures {

    private static final String PATH = "/stations";

    protected ExtractableResponse<Response> 지하철역_생성(String stationName) {
        return 생성(지하철역(stationName), PATH);
    }

    protected List<String> 지하철역_목록조회(String information) {
        return 목록조회(information, PATH);
    }

    protected ExtractableResponse<Response> 지하철역_삭제(String path, String pathVariable) {
        return 삭제(PATH + path, pathVariable);
    }

    protected String 지하철역_생성_값_리턴(String stationName, String value) {
        return 생성_값_리턴(지하철역(stationName), PATH, value);
    }

    private Map<String, String> 지하철역(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return params;
    }
}
