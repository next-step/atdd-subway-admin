package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@DisplayName("지하철 역 관련 Util")
@SuppressWarnings("NonAsciiCharacters")
public class StationAcceptanceUtil extends AcceptanceTest {
    private static final long ZERO = 0L;

    @DisplayName("지하철 역 생성 요청")
    public static long 지하철_역_생성_요청(String station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station);

        ExtractableResponse<Response> response =  RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();

        return response.header("Location") == null ? ZERO : Long.parseLong(response.header("Location").split("/")[2]);
    }

    @DisplayName("지하철 역 조회")
    public static ExtractableResponse<Response> 지하철_역_조회(String station) {
        return RestAssured
                .given().log().all()
                .when().get("/stations/"+station)
                .then().log().all().extract();
    }
}
