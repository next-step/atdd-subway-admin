package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;

public class StationAcceptanceRestAssured {

    public static ExtractableResponse<Response> 지하철역_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static List<ExtractableResponse<Response>> 지하철역들_생성(String... names) {
        return Arrays.stream(names)
                .map(name -> 지하철역_생성(name))
                .collect(Collectors.toList());
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(long id) {
        return RestAssured.given().log().all()
                .when().delete("/stations/{id}", id)
                .then().log().all()
                .extract();
    }
}
