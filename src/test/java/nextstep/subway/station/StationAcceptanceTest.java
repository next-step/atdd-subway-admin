package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> response = StationTestHelper.지하철역_생성(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 모든_지하철역_이름만_조회();
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
        StationRequest 강남역_생성_요청 = new StationRequest("강남역");
        StationTestHelper.지하철역_생성(강남역_생성_요청);

        // when
        ExtractableResponse<Response> response = StationTestHelper.지하철역_생성(강남역_생성_요청);

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
        지하철역_생성됨("강남역");
        지하철역_생성됨("잠실역");

        // when
        List<String> stationName = 모든_지하철역_이름만_조회();

        // then
        assertThat(stationName).hasSize(2)
                .contains("강남역", "잠실역");
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
        long stationsId = 지하철역_생성됨("신천역");

        // when
        StationTestHelper.지하철역_ID로_삭제(stationsId);

        // then
        List<String> stationNames = 모든_지하철역_이름만_조회();
        assertThat(stationNames).doesNotContain("신천역");
    }

    private List<String> 모든_지하철역_이름만_조회() {
        return StationTestHelper.지하철역_전체목록_조회()
                .jsonPath().getList("name", String.class);
    }

    private long 지하철역_생성됨(String name) {
        StationRequest stationRequest = new StationRequest(name);
        return StationTestHelper.지하철역_생성(stationRequest)
                .jsonPath().getLong("id");
    }
}
