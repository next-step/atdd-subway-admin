package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.StationAcceptanceMethod.*;

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
        String 강남역 = "강남역";
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성(강남역);

        // then
        지하철역_생성됨(강남역_생성_응답);

        // then
        생성한_지하철역_찾기(강남역);
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
        String 강남역 = "강남역";
        지하철역_생성(강남역);

        // when
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성(강남역);

        // then
        지하철역_생성_안됨(강남역_생성_응답);
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
        String 잠실역 = "잠실역";
        String 방배역 = "방배역";
        지하철역_생성(잠실역);
        지하철역_생성(방배역);

        // when & then
        생성한_지하철역_찾기(잠실역, 방배역);
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
        String 잠실역 = "잠실역";
        ExtractableResponse<Response> 잠실역_생성_응답 = 지하철역_생성(잠실역);

        // when
        지하철역_삭제(잠실역_생성_응답);

        // then
        지하철역_목록에서_찾을수_없음(잠실역);
    }
}
