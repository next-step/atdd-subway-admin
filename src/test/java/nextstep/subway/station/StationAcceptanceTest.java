package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.LocationUtil;
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
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        List<ExtractableResponse<Response>> givenResponse = 지하철역_등록되어_있음(Arrays.asList("강남역", "역삼역"));

        // when
        ExtractableResponse<Response> whenResponse = 지하철역_모든_리스트_요청();

        // then
        List<Long> expectedLineIds = LocationUtil.getIdsToLocationHeaders(givenResponse);
        List<Long> resultLineIds = getStationResponseIds(whenResponse);

        assertThat(whenResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private static List<ExtractableResponse<Response>> 지하철역_등록되어_있음(List<String> asNames) {
        return asNames.stream()
                .map(StationAcceptanceTest::지하철역_생성_요청)
                .collect(Collectors.toList());
    }

    private static ExtractableResponse<Response> 지하철역_모든_리스트_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse.header("Location"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private static ExtractableResponse<Response> 지하철역_삭제_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
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

    private static List<Long> getStationResponseIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

}
