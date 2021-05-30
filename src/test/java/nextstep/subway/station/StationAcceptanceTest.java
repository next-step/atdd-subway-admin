package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        //given
        String toCreateName = "강남역";

        // when
        ExtractableResponse<Response> result = 지하철_역_등록되어_있음(toCreateName);

        // then
        지하철_역_생성됨(result, toCreateName);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String toCreateName = "강남역";
        지하철_역_등록되어_있음(toCreateName);

        // when
        ExtractableResponse<Response> result = 지하철_역_등록되어_있음(toCreateName);

        // then
        지하철_역_생성_실패됨(result);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_등록되어_있음("강남역");
        ExtractableResponse<Response> createResponse2 = 지하철_역_등록되어_있음("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철_역_목록을_조회한다();

        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(response, createResponse1, createResponse2);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_역_등록되어_있음(String name) {
        StationRequest stationRequest = new StationRequest(name);
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> result, String toCreateName) {
        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(result.header("Location")).isNotBlank();
        StationResponse resultBody = result.as(StationResponse.class);
        assertThat(resultBody.getName()).isEqualTo(toCreateName);
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> result) {
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_역_목록을_조회한다() {
        return RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    private void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, ExtractableResponse<Response>... expects) {
        List<StationResponse> expectResponses = Stream.of(expects)
            .map(it -> it.as(StationResponse.class))
            .collect(Collectors.toList());
        List<StationResponse> actualResponses = response.jsonPath().getList(".", StationResponse.class);
        assertThat(actualResponses).isEqualTo(expectResponses);
    }

}
