package nextstep.subway;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

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

    public static void 요청_성공(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 요청_실패(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 삭제_요청_성공(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
