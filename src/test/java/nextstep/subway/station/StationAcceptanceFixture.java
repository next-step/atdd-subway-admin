package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public class StationAcceptanceFixture {

    public static ExtractableResponse<Response> 지하철_역을_생성한다(String 지하철_역명) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 지하철_역명);

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();
        return response;
    }

    public static List<String> 지하철역_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    public static void 지하철_역을_삭제한다(Long 지하철역_번호) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + 지하철역_번호)
                .then().log().all()
                .extract();
    }

    public static Long 지하철_생성_결과에서_지하철역_번호를_조회한다(ExtractableResponse<Response> 지하철_생성_결과) {
        return 지하철_생성_결과.jsonPath()
                .getLong("id");
    }
}
