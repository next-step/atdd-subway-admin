package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = When.지하철_역_생성_요청("강남역");

        Then.지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        Given.지하철_역_등록되어_있음("강남역");

        ExtractableResponse<Response> response = When.지하철_역_생성_요청("강남역");

        Then.지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response>  createResponse1 = Given.지하철_역_등록되어_있음("강남역");
        ExtractableResponse<Response>  createResponse2 = Given.지하철_역_등록되어_있음("역삼역");

        ExtractableResponse<Response> response = When.지하철역_목록_조회_요청();

        Then.지하철_역_목록_조회됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createResponse = Given.지하철_역_등록되어_있음("강남역");

        ExtractableResponse<Response> response = When.지하철역_제거_요청(createResponse);

        Then.지하철_역_제거됨(response);
    }

    static class Given {
        static ExtractableResponse<Response> 지하철_역_등록되어_있음(String name) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);

            return RestAssured.given()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/stations")
                    .then()
                    .extract();
        }
    }

    static class When {
        static ExtractableResponse<Response> 지하철_역_생성_요청(String name) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);

            return RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/stations")
                    .then().log().all()
                    .extract();
        }

        static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
            return RestAssured.given().log().all()
                    .when()
                    .get("/stations")
                    .then().log().all()
                    .extract();
        }

        static ExtractableResponse<Response> 지하철역_제거_요청(ExtractableResponse<Response> createResponse) {
            String uri = createResponse.header("Location");
            return RestAssured.given().log().all()
                    .when()
                    .delete(uri)
                    .then().log().all()
                    .extract();
        }
    }

    static class Then {
        static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        }

        static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        static void 지하철_역_목록_조회됨(ExtractableResponse<Response> createResponse1,
                                 ExtractableResponse<Response> createResponse2,
                                 ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                    .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                    .collect(Collectors.toList());
            List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                    .map(it -> it.getId())
                    .collect(Collectors.toList());
            assertThat(resultLineIds).containsAll(expectedLineIds);
        }

        static void 지하철_역_제거됨(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }
    }
}
