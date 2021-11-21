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
        // given
        // when
        ExtractableResponse<Response> response = 지하철됨_역_생성_됨("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "역삼역";
        지하철됨_역_생성_됨(stationName);
        Map<String, String> duplicateParams = 지하철_역_생성_파라미터_맵핑(stationName);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(duplicateParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철됨_역_생성_됨("강남역");
        ExtractableResponse<Response> createResponse2 = 지하철됨_역_생성_됨("역삼역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        // then
        List<Long> expectedLineIds = ids_추출_By_Location(createResponse1, createResponse2);
        List<Long> resultLineIds = ids_추출_By_StationResponse(response);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철됨_역_생성_됨("강남역");
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_함(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> 지하철_역_생성_파라미터_맵핑(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return params;
    }

    private ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }


    private ExtractableResponse<Response> 지하철됨_역_생성_됨(String stationName) {
        Map<String, String> params = 지하철_역_생성_파라미터_맵핑(stationName);

        return 지하철_역_생성_요청(params);
    }

    private ExtractableResponse<Response> 지하철_역_제거_함(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }


    private List<Long> ids_추출_By_StationResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
    }

    private List<Long> ids_추출_By_Location(ExtractableResponse<Response> createResponse1,
        ExtractableResponse<Response> createResponse2) {
        return Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

}
