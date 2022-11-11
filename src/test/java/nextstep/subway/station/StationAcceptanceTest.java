package nextstep.subway.station;

import static nextstep.subway.utils.StationAcceptanceTestUtils.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // when
        ExtractableResponse<Response> 저장된_잠실역 = 지하철역을_생성한다(잠실역);

        // then
        지하철역_생성을_확인한다(저장된_잠실역);

        // then
        List<String> 조회된_지하철역_목록 = 지하철_목록을_조회한다();
        // 첫 번째 변수는 실제 반환된 리스트, 두번째 부터는 검증할 지하철 역명을 입력한다.
        지하철_목록_검증_입력된_지하철역이_존재(조회된_지하철역_목록, 잠실역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    void createStationWithDuplicateName() {
        // given
        지하철역을_생성한다(잠실역);

        // when
        ExtractableResponse<Response> 저장된_잠실역 = 지하철역을_생성한다(잠실역);

        // then
        지하철역_생성_실패를_확인한다(저장된_잠실역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        // given
        지하철역을_생성한다(잠실역);
        지하철역을_생성한다(몽촌토성역);

        // when
        List<String> 조회된_지하철역_목록 = 지하철_목록을_조회한다();

        // then
        지하철_목록_검증_입력된_지하철역이_존재(조회된_지하철역_목록, 잠실역, 몽촌토성역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        ExtractableResponse<Response> 저장된_잠실역 = 지하철역을_생성한다(잠실역);

        // when
        지하철_역을_제거한다(저장된_잠실역);

        // then
        List<String> 조회된_지하철역_목록 = 지하철_목록을_조회한다();
        지하철_목록_검증_입력된_지하철역이_존재하지_않음(조회된_지하철역_목록, 잠실역);
    }
}
