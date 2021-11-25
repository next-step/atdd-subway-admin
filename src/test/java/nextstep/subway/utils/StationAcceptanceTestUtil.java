package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.station.dto.StationResponse;
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

    public static List<Long> ids_추출_By_Location(
        List<ExtractableResponse<Response>> createResponses) {
        return createResponses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }
}
