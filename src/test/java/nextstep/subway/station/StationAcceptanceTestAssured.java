package nextstep.subway.station;

import java.util.List;
import java.util.Map;

import org.apache.groovy.util.Maps;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.RestAssuredUtils;

public class StationAcceptanceTestAssured {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String REQUEST_PATH = "/stations";

    public static List<String> 지하철역_목록_조회() {
        return RestAssuredUtils.getAll(REQUEST_PATH, NAME);
    }

    public static ExtractableResponse<Response> 지하철역_생성(String 지하철역_이름) {
        return RestAssuredUtils.post(REQUEST_PATH, 지하철역_요청_파라미터(지하철역_이름));
    }

    public static Map<String, String> 지하철역_요청_파라미터(String 지하철역_이름) {
        return Maps.of(NAME, 지하철역_이름);
    }

    public static void 지하철역_삭제(ExtractableResponse<Response> 생성_응답) {
        Long 지하철역_식별자 = 지하철역_식별자(생성_응답);
        RestAssuredUtils.delete(REQUEST_PATH, 지하철역_식별자);
    }

    public static Long 지하철역_식별자(ExtractableResponse<Response> 지하철역_생성_응답) {
        String 응답본문 = 지하철역_생성_응답.response().getBody().print();
        return JsonPath.from(응답본문).getLong(ID);
    }

}
