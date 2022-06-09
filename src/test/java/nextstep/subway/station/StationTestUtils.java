package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTestUtils {
    public static final String PATH = "/stations";

    public static ExtractableResponse<Response> 지하철역_생성_요청(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
                .when().get(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete(PATH + "/{id}")
                .then().log().all()
                .extract();
    }

    public static Long 지하철역_ID_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static void 지하철역_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철역_생성_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_조회_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철역_삭제_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철역_포함_확인(String stationName) {
        List<String> stationNames = 지하철역_목록_조회().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf(stationName);
    }

    public static void 지하철역_미포함_확인(String stationName) {
        List<String> stationNames = 지하철역_목록_조회().jsonPath().getList("name", String.class);
        assertThat(stationNames).doesNotContain(stationName);
    }
}
