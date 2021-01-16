package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@DisplayName("지하철 역 관련 Util")
@SuppressWarnings("NonAsciiCharacters")
public class StationAcceptanceUtil extends AcceptanceTest {

    @DisplayName("지하철 역 생성 요청")
    public static Long 지하철_역_생성_요청(String station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station);

        ExtractableResponse<Response> response =  RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();

        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
