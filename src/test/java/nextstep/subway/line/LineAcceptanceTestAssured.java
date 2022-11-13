package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.RestAssuredUtils;
import org.apache.groovy.util.Maps;

import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_식별자;

public class LineAcceptanceTestAssured {

    static final String ID = "id";
    static final String NAME = "name";
    static final String COLOR = "color";
    static final String DISTANCE = "distance";
    static final String DEFAULT_COLOR = "bg-red-600";
    static final int DEFAULT_DISTANCE = 10;
    static final String UP_STATION_ID = "upStationId";
    static final String DOWN_STATION_ID = "downStationId";
    static final String REQUEST_PATH = "/lines";

    public static LineAcceptanceTestResponse 지하철_노선_생성(LineAcceptanceTestRequest 노선요청정보) {

        long 상행역_식별자 = 지하철역_식별자(지하철역_생성(노선요청정보.상행종점역));
        long 하행역_식별자 = 지하철역_식별자(지하철역_생성(노선요청정보.하행종점역));
        long 노선_식별자 = 지하철_노선_식별자(지하철_노선_생성(노선요청정보.노선, 상행역_식별자, 하행역_식별자, 노선요청정보.거리));
        return new LineAcceptanceTestResponse(노선_식별자, 상행역_식별자, 하행역_식별자);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(String 노선_이름, String 상행역_이름, String 하행역_이름) {
        long 상행역_식별자 = 지하철역_식별자(지하철역_생성(상행역_이름));
        long 하행역_식별자 = 지하철역_식별자(지하철역_생성(하행역_이름));
        return 지하철_노선_생성(노선_이름, 상행역_식별자, 하행역_식별자, DEFAULT_DISTANCE);
    }

    static ExtractableResponse<Response> 지하철_노선_생성(String 노선_이름, long 상행역_아이디, long 하행역_아이디, int 거리) {
        Map<String, String> 요청_본문 = 지하철_노선_파라미터(노선_이름, DEFAULT_COLOR, 상행역_아이디, 하행역_아이디, 거리);

        return RestAssuredUtils.post(REQUEST_PATH, 요청_본문);
    }

    static List<String> 지하철_노선_목록_조회() {
        return RestAssuredUtils.getAll(REQUEST_PATH, NAME);
    }

    static String 지하철_노선_조회(ExtractableResponse<Response> 지하철_노선_생성_응답) {
        return RestAssuredUtils.get(REQUEST_PATH, 지하철_노선_식별자(지하철_노선_생성_응답), NAME);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(long 식별자_아이디) {
        return RestAssuredUtils.get(REQUEST_PATH, 식별자_아이디);
    }

    static ExtractableResponse<Response> 지하철_노선_수정(ExtractableResponse<Response> 지하철_노선_생성_응답, String 수정할_지하철_노선_이름) {
        Map<String, String> 요청_본문 = 지하철_노선_파라미터(수정할_지하철_노선_이름, DEFAULT_COLOR, DEFAULT_DISTANCE);
        return RestAssuredUtils.put(REQUEST_PATH, 지하철_노선_식별자(지하철_노선_생성_응답), 요청_본문);
    }

    static ExtractableResponse<Response> 지하철_노선_삭제(ExtractableResponse<Response> 지하철_노선_생성_응답) {
        return RestAssuredUtils.delete(REQUEST_PATH, 지하철_노선_식별자(지하철_노선_생성_응답));
    }

    private static Map<String, String> 지하철_노선_파라미터(String 노선_이름, String 노선_색상, int 거리) {
        return Maps.of(NAME, 노선_이름, COLOR, 노선_색상, DISTANCE, 거리 + "");
    }

    private static Map<String, String> 지하철_노선_파라미터(String 노선_이름, String 노선_색상, Long 상행역_아이디, Long 하행역_아이디, int 거리) {
        return Maps.of(
            NAME, 노선_이름,
            COLOR, 노선_색상,
            UP_STATION_ID, 상행역_아이디 + "",
            DOWN_STATION_ID, 하행역_아이디 + "",
            DISTANCE, 거리 + "");
    }

    public static long 지하철_노선_식별자(ExtractableResponse<Response> 지하철_노선_생성_응답) {
        return 지하철_노선_생성_응답.jsonPath().getLong(ID);
    }
}
