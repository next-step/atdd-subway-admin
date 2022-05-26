package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class AcceptanceTestFactory {
    private static final Map<String, Object> params = new HashMap<>();

    public static ExtractableResponse<Response> 지하철_역_생성(String name) {
        params.put("name", name);
        return RestAssuredTemplate.sendPost("/stations", params);
    }

    public static Long 지하철_역_생성_ID_추출(String name) {
        return 지하철_역_생성(name).jsonPath().getObject("id", Long.class);
    }

    public static List<String> 지하철_역_목록_조회() {
        return RestAssuredTemplate.sendGet("/stations").jsonPath().getList("name", String.class);
    }

    public static void 지하철_역_삭제_요청(Long id) {
        RestAssuredTemplate.sendDelete("/stations/{id}", id);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(String name) {
        params.put("name", name);
        params.put("color", "bg-red-600");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        return RestAssuredTemplate.sendPost("/lines", params);
    }

    public static void 생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 생성_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 목록_조회_성공_확인(List<String> names, String... name) {
        assertThat(names).contains(name);
    }

    public static void 목록_조회_실패_확인(List<String> names, String... name) {
        assertThat(names).doesNotContain(name);
    }
}
