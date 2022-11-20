package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.request.StationApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 인수 테스트")
class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = StationApi.createStation("강남역");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<String> stationNames = getList(StationApi.getStations(), "name");
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        StationApi.createStation("강남역");

        ExtractableResponse<Response> response = StationApi.createStation("강남역");

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
        StationApi.createStation("강남역");
        StationApi.createStation("서울역");

        List<String> stations = getList(StationApi.getStations(), "name");
        assertThat(stations).contains("강남역", "서울역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Long stationId = getId(StationApi.createStation("강남역"));

        ExtractableResponse<Response> response = StationApi.deleteStation(stationId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<String> stations = getList(StationApi.getStations(), "name");
        assertThat(stations).isEmpty();
    }
}
