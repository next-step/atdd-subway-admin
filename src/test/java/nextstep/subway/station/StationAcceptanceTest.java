package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.StationTestUtils.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_성공_확인(생성_응답);

        // then
        지하철역_포함_확인("강남역");
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
        ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_실패_확인(생성_응답);
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
        ExtractableResponse<Response> 조회_응답 = 지하철역_목록_조회();

        // then
        지하철역_조회_성공_확인(조회_응답);

        // then
        지하철역_포함_확인("강남역");
        지하철역_포함_확인("잠실역");
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
        ExtractableResponse<Response> 생성_응답 = 지하철역_생성_요청("강남역");
        Long 역_ID = 지하철역_ID_조회(생성_응답);

        // when
        ExtractableResponse<Response> 삭제_응답 = 지하철역_삭제_요청(역_ID);

        // then
        지하철역_삭제_성공_확인(삭제_응답);

        // then
        지하철역_미포함_확인("강남역");
    }
}
