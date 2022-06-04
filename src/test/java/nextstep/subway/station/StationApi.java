package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractIntegerAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationApi {
    public static ExtractableResponse<Response> 지하철역_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> createdResponse =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

        지하철역_생성_검증(createdResponse);

        return createdResponse;
    }

    public static void 지하철역_생성_검증(ExtractableResponse<Response> createdResponse) {
        assertThat(createdResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 지하철역_조회() {
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract();

        지하철역_조회_검증(response);

        return response;
    }

    public static void 지하철역_조회_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철역_제거(Long 역Id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + 역Id)
                .then().log().all()
                .extract();

        지하철역_제거_검증(response);
    }

    public static AbstractIntegerAssert<?> 지하철역_제거_검증(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
