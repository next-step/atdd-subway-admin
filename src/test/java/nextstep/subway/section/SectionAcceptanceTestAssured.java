package nextstep.subway.section;

import org.apache.groovy.util.Maps;

import java.util.Map;

import static java.lang.String.format;
import static nextstep.subway.RestAssuredUtils.post;

class SectionAcceptanceTestAssured {

    static String DISTANCE = "distance";
    static String UP_STATION_ID = "upStationId";
    static String DOWN_STATION_ID = "downStationId";

    static void 구간_등록(SectionAcceptanceTestRequest 구간요청정보) {
        post(요청_주소(구간요청정보.노선_식별자), 요청_본문(구간요청정보));
    }

    private static Map<String, String> 요청_본문(SectionAcceptanceTestRequest 구간요청정보) {
        return Maps.of(
                UP_STATION_ID, 구간요청정보.상행역_식별자 + "",
                DOWN_STATION_ID, 구간요청정보.하행역_식별자 + "",
                DISTANCE, 구간요청정보.거리 + "");
    }

    private static String 요청_주소(long 노선_식별자) {
        return format("/lines/%s/sections", 노선_식별자);
    }
}
