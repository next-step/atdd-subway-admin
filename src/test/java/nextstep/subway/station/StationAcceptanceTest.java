package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceTestActions.지하철역_목록조회;
import static nextstep.subway.station.StationAcceptanceTestActions.지하철역_삭제;
import static nextstep.subway.station.StationAcceptanceTestActions.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestActions.지하철역_생성_값_리턴;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.domain.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * <p>
     * Then 지하철역이 생성된다
     * <p>
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철역_목록조회("name");
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
        지하철역_생성("강남역");

        //when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

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
        //given
        지하철역_생성("경기 광주역");
        지하철역_생성("중앙역");

        // when
        List<String> stationNames = 지하철역_목록조회("name");

        //then
        assertThat(stationNames).contains("경기 광주역", "중앙역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        String id = 지하철역_생성_값_리턴("판교역", "id");

        //when
        ExtractableResponse<Response> response = 지하철역_삭제("/{id}", id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
