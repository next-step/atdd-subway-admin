package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.groovy.util.Maps;

import java.util.Map;

import static java.lang.String.format;
import static nextstep.subway.RestAssuredUtils.delete;
import static nextstep.subway.RestAssuredUtils.post;

class SectionAcceptanceTestAssured {

    static String DISTANCE = "distance";
    static String UP_STATION_ID = "upStationId";
    static String DOWN_STATION_ID = "downStationId";

    static ExtractableResponse<Response> 구간_등록(long 노선_식별자, long 상행역_식별자, long 하행역_식별자, int 거리) {
        return post(요청_주소(노선_식별자), 요청_본문(상행역_식별자, 하행역_식별자, 거리));
    }

    private static Map<String, String> 요청_본문(long 상행역_식별자, long 하행역_식별자, int 거리) {
        return Maps.of(
                UP_STATION_ID, 상행역_식별자 + "",
                DOWN_STATION_ID, 하행역_식별자 + "",
                DISTANCE, 거리 + "");
    }

    public static ExtractableResponse<Response> 구간_제거(long 노선_식별자, long 역_식별자) {
        return delete(삭제_요청_주소(노선_식별자, 역_식별자));
    }

    private static String 요청_주소(long 노선_식별자) {
        return format("/lines/%s/sections", 노선_식별자);
    }

    private static String 삭제_요청_주소(long 노선_식별자, long 역_식별자) {
        return format("/lines/%s/sections?stationId=%s", 노선_식별자, 역_식별자);
    }
}
