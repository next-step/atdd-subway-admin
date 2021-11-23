package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String path = "/stations";
    private StationRequest request1;
    private StationRequest request2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        request1 = new StationRequest("강남역");
        request2 = new StationRequest("역삼역");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성(request1);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역 생성 할 수 없다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(request1);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성(request1);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        long stationId1 = 지하철_역_등록되어_있음(request1);
        long stationId2 = 지하철_역_등록되어_있음(request2);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회();

        // then
        지하철역_목록_포함됨(response, Arrays.asList(stationId1, stationId2));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        long stationId = 지하철_역_등록되어_있음(request1);

        // when
        ExtractableResponse<Response> response = 지하철_역_제거(stationId);

        // then
        지하철_역_삭제됨(response);
    }

    private long 지하철_역_등록되어_있음(StationRequest request) {
        ExtractableResponse<Response> response = 지하철_역_생성(request);
        return response.jsonPath().getLong("id");
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> 지하철_역_생성(StationRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    private void 지하철역_목록_포함됨(ExtractableResponse<Response> response, List<Long> expectedIds) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedIds);
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
        return response;
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_역_제거(long stationId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(path + String.format("/%d", stationId))
            .then().log().all()
            .extract();
        return response;
    }
}
