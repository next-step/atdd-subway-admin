package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(new StationRequest("강남역"));

        // then
        지하철역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청(new StationRequest("강남역"));

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(new StationRequest("강남역"));

        // then
        지하철역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청(new StationRequest("강남역"));
        ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청(new StationRequest("역삼역"));

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회();

        // then
        지하철역_조회됨(response);
        생성된_지하철역_조회_검증(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(new StationRequest("강남역"));

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철역_제거_요청(uri);

        // then
        지하철역_제거됨(response);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 지하철역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철역_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 생성된_지하철역_조회_검증(ExtractableResponse<Response> createResponse1,
                                      ExtractableResponse<Response> createResponse2,
                                      ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Stream.of(생성된_지하철역(createResponse1), 생성된_지하철역(createResponse2))
                .map(StationResponse::getId)
                .collect(toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static StationResponse 생성된_지하철역(ExtractableResponse<Response> createResponse) {
        return createResponse.as(StationResponse.class);
    }
}
