package nextstep.subway.station.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        StationAcceptance.create_station("강남역");

        // then
        List<String> stationNames = StationAcceptance.station_list_was_queried();
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
        // given
        StationAcceptance.create_station("강남역");

        // when
        ExtractableResponse<Response> response = StationAcceptance.create_station("강남역");

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * When 지하철역 이름을 null로 입력해 지하철역을 생성하면
     * Then 지하철역을 생성할 수 없다
     */
    @DisplayName("지하철역 이름으로 null을 입력해 지하철역을 생성한다.")
    @Test
    void createStationWithNullTypeName() {
        // when
        ExtractableResponse<Response> response = StationAcceptance.create_station(null);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }


    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        StationAcceptance.create_station("강남역");
        StationAcceptance.create_station("역삼역");

        // when
        List<String> stationNames = StationAcceptance.station_list_was_queried();

        // then
        assertThat(stationNames).containsAll(Arrays.asList("강남역", "역삼역"));
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
        Long id = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));

        // when
        StationAcceptance.delete_station(id);

        // then
        List<String> stationNames = StationAcceptance.station_list_was_queried();
        assertThat(stationNames).doesNotContain("강남역");
    }
}
