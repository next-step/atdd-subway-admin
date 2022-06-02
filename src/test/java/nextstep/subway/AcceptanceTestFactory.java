package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class AcceptanceTestFactory {
    public static ExtractableResponse<Response> 지하철_역_생성(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return RestAssuredTemplate.post("/stations", params);
    }

    public static Long 지하철_역_생성_ID_추출(String name) {
        return 지하철_역_생성(name).jsonPath().getObject("id", Long.class);
    }

    public static void 생성된_지하철_역_찾기(String... name) {
        List<String> 지하철역_목록 = 지하철_역_목록_조회();
        목록_조회_성공_확인(지하철역_목록, name);
    }

    public static void 생성된_지하철_역_찾을_수_없음(String... name) {
        List<String> 지하철역_목록 = 지하철_역_목록_조회();
        목록_조회_실패_확인(지하철역_목록, name);
    }

    public static List<String> 지하철_역_목록_조회() {
        return RestAssuredTemplate.get("/stations").jsonPath().getList("name", String.class);
    }

    public static void 지하철_역_삭제(Long id) {
        RestAssuredTemplate.deleteWithId("/stations/{id}", id);
    }

    // line related methods

    public static void 목록_조회_성공_확인(List<String> names, String... name) {
        assertThat(names).contains(name);
    }

    public static void 목록_조회_실패_확인(List<String> names, String... name) {
        assertThat(names).doesNotContain(name);
    }

    public static void 생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 생성_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 수정_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 삭제_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
