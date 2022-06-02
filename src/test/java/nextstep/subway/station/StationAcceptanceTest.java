package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.AcceptanceTestFactory.*;

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
        String 강남역 = "강남역";
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성(강남역);

        생성_성공_확인(강남역_생성_응답);

        생성된_지하철_역_찾기(강남역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        String 강남역 = "강남역";
        지하철_역_생성(강남역);

        ExtractableResponse<Response> 강남역_생성응답 = 지하철_역_생성(강남역);

        생성_실패_확인(강남역_생성응답);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        String 논현역 = "논현역";
        String 신논현역 = "신논현역";
        지하철_역_생성(논현역);
        지하철_역_생성(신논현역);

        생성된_지하철_역_찾기(논현역, 신논현역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        String 논현역 = "논현역";
        Long 논현역_생성_ID = 지하철_역_생성_ID_추출(논현역);

        지하철_역_삭제(논현역_생성_ID);

        생성된_지하철_역_찾을_수_없음(논현역);
    }
}
