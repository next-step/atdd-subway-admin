package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DataBaseCleaner;
import nextstep.subway.rest.StationRestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dataBaseCleaner.clear();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = StationRestAssured.지하철_역_생성("고속터미널역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> findAllStationsResponse = StationRestAssured.지하철_역_목록_조회();
        List<String> stationNames = findAllStationsResponse.jsonPath().getList("name", String.class);
        assertAll(
                () -> assertThat(findAllStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stationNames).containsAnyOf("고속터미널역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRestAssured.지하철_역_생성("강남역");

        // when
        ExtractableResponse<Response> response = StationRestAssured.지하철_역_생성("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        StationRestAssured.지하철_역_생성("홍대역");
        StationRestAssured.지하철_역_생성("이수역");

        // when
        ExtractableResponse<Response> response = StationRestAssured.지하철_역_목록_조회();

        // then
        List<String> stationNames = response.jsonPath().getList("name", String.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stationNames).containsExactly("홍대역", "이수역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createStationResponse = StationRestAssured.지하철_역_생성("영등포역");
        long newStationId = createStationResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteStationResponse = StationRestAssured.지하철_역_삭제(newStationId);

        // then
        ExtractableResponse<Response> findStationsResponse = StationRestAssured.지하철_역_목록_조회();
        List<Long> stationIds = findStationsResponse.jsonPath().getList("id", Long.class);
        assertAll(
                () -> assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(stationIds).doesNotContain(newStationId)
        );
    }
}
