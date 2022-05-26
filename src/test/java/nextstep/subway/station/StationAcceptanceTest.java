package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.RequestStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    @LocalServerPort
    int port;

    private RequestStation requestStation = new RequestStation();

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void request() {
        // when
        final String creatStationName = "강남역";

        Station savedStation = 지하철역_생성(creatStationName).as(Station.class);

        //Then
        assertThat(savedStation.getName()).isEqualTo(creatStationName);
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
        final String creatStationName = "강남역";
        final Station savedStation = 지하철역_생성(creatStationName).as(Station.class);

        //when
        assertThat(savedStation.getName()).isEqualTo(creatStationName);

        //Then
        assertThat(HttpStatus.valueOf(지하철역_생성(creatStationName).statusCode())).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * When 등록된 지하철 역이 없을때 지하철 역을 검색 하면
     * Then 지하철 역 목록 정보가 없다.
     */
    @DisplayName("지하철 역 등록 을 하지 않고 조회 시 검색 결과가 없다.")
    @Test
    void noCreateAndSearch() {
        // when
        ExtractableResponse<Response> response = 지하철역_검색();

        // then
        assertThat(HttpStatus.valueOf(response.statusCode())).isEqualTo(HttpStatus.OK);
        assertThat(response.jsonPath().getList(".", Station.class).isEmpty()).isTrue();
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철 역 등록 후 지하철 역 조회 등록 순서 대로 조회 된다.")
    @Test
    void createAndSearch() {
        List<Station> createdStations = new ArrayList<>();
        // given
        createdStations.add(지하철역_생성("지하철역이름").as(Station.class));
        createdStations.add(지하철역_생성("새로운지하철역이름").as(Station.class));
        createdStations.add(지하철역_생성("또다른지하철역이름").as(Station.class));

        // when
        List<Station> searchResult = 지하철역_검색().jsonPath().getList(".", Station.class);

        // then
        assertThat(searchResult.toArray(new Station[0])).containsExactly(createdStations.toArray(new Station[0]));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철 역을 등록 한 후 특정 지하철 역을 삭제 하고 조회 시 해당 역을 제외 하고 검색 된다.")
    @Test
    void 등록_삭제_검색() {
        // given
        final Station savedStation = 지하철역_생성("강남역").as(Station.class);

        // when
        지하철역_삭제(savedStation.getId());

        // then
        Optional<Station> isStation = 지하철역_검색().jsonPath().getList(".", Station.class).stream()
                .filter(station -> Objects.equals(station.getName(), savedStation.getName()))
                .findAny();

        assertThat(isStation.isPresent()).isFalse();
    }

    /**
     * Given 등록되지 않는 상태인지 지하철역을 검색하고
     * When 해당 지하철역을 삭제하면
     * Then 삭제가 되지 않는다.
     */

    @DisplayName("지하철 역 삭제 시 해당 역 정보가 없으면 삭제가 되지 않는다.")
    @Test
    void noContentDeleteStation() {
        final Long id = 100L;
        // Given
        assertThat(
                지하철역_검색().jsonPath().getList(".", Station.class)
                .stream().anyMatch(station -> Objects.equals(station.getId(), id))).isFalse();

        // when
        ExtractableResponse<Response> response = 지하철역_삭제(id);

        // then
        assertThat(HttpStatus.valueOf(response.statusCode())).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 지하철역_생성(final String stationName) {
        return requestStation.createStation(stationName);
    }

    private ExtractableResponse<Response> 지하철역_검색() {
        return requestStation.getStations();
    }

    private ExtractableResponse<Response> 지하철역_삭제(final Long index) {
        return requestStation.deleteStation(index);
    }

}
