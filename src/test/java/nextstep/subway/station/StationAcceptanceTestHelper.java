package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class StationAcceptanceTestHelper {
    private StationAcceptanceTestHelper() {
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    public static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.CREATED.value());

        assertThat(response.header("Location"))
            .isNotBlank();
    }

    public static Station 지하철_역_등록되어_있음(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);

        return response.as(Station.class);
    }

    public static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    public static void 지하철_역_목록_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, List<Long> expectedStaionIds) {
        List<Long> resultLineIds = response
            .jsonPath()
            .getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedStaionIds);
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(long id) {
        return RestAssured
            .given().log().all()
            .when()
            .delete("/stations/" + id)
            .then().log().all()
            .extract();
    }

    public static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
