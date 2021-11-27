package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class StationAcceptanceTestUtil {

    private StationAcceptanceTestUtil() {
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철됨_역_생성_됨(String stationName) {
        Map<String, String> params = 지하철_역_생성_파라미터_맵핑(stationName);

        return 지하철_역_생성_요청(params);
    }


    public static Long 지하철됨_역_생성_됨_toId(String stationName) {
        Map<String, String> params = 지하철_역_생성_파라미터_맵핑(stationName);
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);
        return response.as(StationResponse.class).getId();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_제거_함(
        ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    public static Map<String, String> 지하철_역_생성_파라미터_맵핑(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return params;
    }

    public static List<Long> ids_추출_By_StationResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
    }

    public static void 지하철역_응답_검증(ExtractableResponse<Response> createResponse,
        HttpStatus created) {
        assertThat(createResponse.statusCode()).isEqualTo(created.value());
    }

    public static void 지하철_등록_성공(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    public static void 지하철_역_목록_조회에_역ID_포함됨(List<Long> createdStationIds,
        ExtractableResponse<Response> stationsListResponse) {
        assertThat(ids_추출_By_StationResponse(stationsListResponse)).containsAll(createdStationIds);
    }
}
