package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.util.StationTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> createResponse = StationTestUtil.createStation("강남역");

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> response = StationTestUtil.getAllStations();
        assertThat(response.jsonPath().getList("name", String.class)).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationTestUtil.createStation("강남역");

        // when
        ExtractableResponse<Response> response = StationTestUtil.createStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        StationTestUtil.createStation("강남역");
        StationTestUtil.createStation("삼성역");

        // when
        ExtractableResponse<Response> response = StationTestUtil.getAllStations();

        // then
        assertThat(response.jsonPath().getList("$", StationResponse.class)).hasSize(2);
        assertThat(response.jsonPath().getList("name", String.class)).contains("강남역", "삼성역");
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = StationTestUtil.createStation("강남역");

        // when
        StationTestUtil.deleteStation(createResponse.jsonPath().getLong("id"));

        // then
        ExtractableResponse<Response> response = StationTestUtil.getAllStations();
        assertThat(response.jsonPath().getList("name", String.class)).doesNotContain("강남역");
    }
}
