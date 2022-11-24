package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class StationSteps {

    public static final String GANGNAM_STATION = "강남역";
    public static final String YUKSAM_STATION = "역삼역";
    public static final String SHINNONHYUN_STATION = "신논현역";
    public static final String NONHYUN_STATION = "논현역";
    public static final String SEOLLEUNG_STATION = "선릉역";

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> param = new HashMap<>();
        param.put("name", stationName);
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철역_삭제(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    public static void 역_삭제_검증(ExtractableResponse<Response> response, String stationName) {
        List<String> stationNames = 지하철역_목록_조회().jsonPath().getList("name");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(stationNames).doesNotContain(stationName)
        );
    }
}
