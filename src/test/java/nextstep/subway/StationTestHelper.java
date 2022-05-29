package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class StationTestHelper {

    public static ExtractableResponse<Response> 역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 역_조회_요청() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static void 역_포함_확인(ExtractableResponse<Response> response, String... stationNames) {
        List<String> responseNames = response.jsonPath().getList("name", String.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.statusCode()),
            () -> assertThat(responseNames).containsAll(Arrays.asList(stationNames))
        );
    }

    public static void 역_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 역_제거_요청(Long id) {
        RestAssured.given().log().all()
            .when().delete("/stations/{id}", id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 구간_제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
            .param("stationId", stationId)
            .when().delete("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

    public static void 구간_제거_확인(ExtractableResponse<Response> response, String removeStationName) {
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.statusCode()),
            () -> assertThat(stationNames).doesNotContain(removeStationName)
        );
    }

    public static void 구간_제거_실패_확인(ExtractableResponse<Response> response) {
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

}
