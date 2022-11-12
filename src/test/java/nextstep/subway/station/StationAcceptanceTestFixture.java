package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTestFixture {

    public static final String BASE_URL = "/stations";
    public static final String PATH_VARIABLE_STATION_ID = "/{id}";

    public static ExtractableResponse<Response> 지하철역_생성(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_URL)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_전체_목록() {
        return RestAssured.given().log().all()
                .when().get(BASE_URL)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(final Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(BASE_URL + PATH_VARIABLE_STATION_ID, id)
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철역_이름_전체_목록() {
        return RestAssured.given().log().all()
                .when().get(BASE_URL)
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);
    }

    public static void 지하철역_목록에_지하철역_이름이_포함되어_있다(List<String> 지하철역_이름_목록, String... 지하철역_이름) {
        assertThat(지하철역_이름_목록).containsAnyOf(지하철역_이름);
    }

    public static void 지하철역_목록에_지하철역이_존재하지_않는다(List<String> 지하철역_이름_목록, String 지하철역_이름) {
        assertThat(지하철역_이름_목록).doesNotContain(지하철역_이름);
    }
}
