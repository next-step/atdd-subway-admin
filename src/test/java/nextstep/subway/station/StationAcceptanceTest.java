package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationAssuredMethod.지하철역_삭체_요청;
import static nextstep.subway.station.StationAssuredMethod.지하철역_생성_요청;
import static nextstep.subway.station.StationAssuredMethod.지하철역_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_성공_확인(강남역_생성_응답);

        // then
        List<String> 지하철역_이름_목록 = 지하철역_이름_목록을_구한다();
        역이름_포함_확인(지하철역_이름_목록, "강남역");
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
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_실패_확인(강남역_생성_응답);
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
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("잠실역");

        // when
        List<String> 생성된_지하철역_이름_목록 = 지하철역_이름_목록을_구한다();

        // then
        역이름_포함_확인(생성된_지하철역_이름_목록, "강남역", "잠실역");
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
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청("강남역");

        //when
        long 생성역_id = 생성역_id_추출(강남역_생성_응답);
        지하철역_삭체_요청(생성역_id);

        //then
        List<String> 지하철역_이름_목록 = 지하철역_이름_목록을_구한다();
        역이름_불포함_확인(지하철역_이름_목록, "강남역");
    }

    private long 생성역_id_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    private void 지하철역_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철역_생성_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 역이름_포함_확인(List<String> stationNames, String... stationName) {
        assertThat(stationNames).containsAll(Arrays.asList(stationName));
    }

    private void 역이름_불포함_확인(List<String> stationNames, String stationName) {
        assertThat(stationNames).doesNotContain(stationName);
    }

    private List<String> 지하철역_이름_목록을_구한다() {
        return 지하철역_조회_요청().jsonPath().getList("name", String.class);
    }
}
