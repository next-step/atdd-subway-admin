package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTestFixture {

    public static final String BASE_URL = "/lines";
    public static final String PATH_VARIABLE_LINE_ID = "/{id}";

    public static ExtractableResponse<Response> 지하철_노선_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_URL)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(final Long id) {
        return RestAssured.given().log().all()
                .when().get(BASE_URL + PATH_VARIABLE_LINE_ID, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(final Long id, final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(BASE_URL + PATH_VARIABLE_LINE_ID, id)
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철_노선_이름_전체_목록() {
        return RestAssured.given().log().all()
                .when().get(BASE_URL)
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);
    }

    public static void 지하철_노선_목록에_생성한_노선이_포함되어_있다(List<String> 지하철_노선_목록, String... 지하철_노선_이름) {
        assertThat(지하철_노선_목록).containsExactly(지하철_노선_이름);
    }

    public static void 지하철_노선_이름을_검증한다(String 지하철_노선_목록, String 지하철_노선_이름) {
        assertThat(지하철_노선_목록).isEqualTo(지하철_노선_이름);
    }

    public static void 수정된_지하철_노선_정보를_검증한다(ExtractableResponse<Response> 지하철_노선_조회, String 변경된_지하철_노선_이름, String 변경된_지하철_노선_색) {
        assertThat(지하철_노선_조회.jsonPath().getString("name")).isEqualTo(변경된_지하철_노선_이름);
        assertThat(지하철_노선_조회.jsonPath().getString("color")).isEqualTo(변경된_지하철_노선_색);
    }
}
