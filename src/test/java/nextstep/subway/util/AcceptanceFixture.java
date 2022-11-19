package nextstep.subway.util;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AcceptanceFixture {

    public static Long 아이디_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static List<Long> 목록_아이디_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("id");
    }

    public static List<String> 목록_이름_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name");
    }

    public static String 이름_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

    public static boolean 결과에_존재한다(ExtractableResponse<Response> response, String...조회대상) {
        return response.jsonPath().getList("name", String.class).containsAll(Arrays.asList(조회대상));
    }
}
