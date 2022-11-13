package nextstep.subway;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

public class AcceptanceFixture {

    public static JsonPath 제이슨_경로_얻기(ExtractableResponse<Response> response) {
        return response.jsonPath();
    }

    public static Long 식별_아이디_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static List<String> 목록_이름_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name");
    }

    public static String 이름_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

}
