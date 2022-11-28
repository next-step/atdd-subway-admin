package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.utils.StationAcceptanceTestUtil.*;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        String 지하철역 = "용산역";

        // then
        지하철역_생성_성공_확인(지하철역_생성(지하철역));

        // then
        List<String> 조회된_지하철역_목록 = 지하철_목록_조회();
        지하철_목록_검증_입력된_지하철역이_존재(조회된_지하철역_목록, "용산역");
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
        String 지하철역 = "노량진역";
        지하철역_생성(지하철역);

        // when
        ExtractableResponse<Response> 저장된_노량진역 = 지하철역_생성(지하철역);

        // then
        지하철역_생성_실패_확인(저장된_노량진역);
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
        String 지하철역 = "노량진역";
        String 지하철역2 = "고속터미널역";
        지하철역_생성(지하철역);
        지하철역_생성(지하철역2);

        // when
        List<String> 조회된_지하철역_목록 = 지하철_목록_조회();

        // then
        지하철_목록_검증_입력된_지하철역이_존재(조회된_지하철역_목록, 지하철역, 지하철역2);
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
        String 지하철역 = "노량진역";
        ExtractableResponse<Response> 저장된_노량진역 = 지하철역_생성(지하철역);

        // when
        지하철역_제거(저장된_노량진역);

        // then
        List<String> 조회된_지하철역_목록 = 지하철_목록_조회();
        지하철_목록_검증_입력된_지하철역이_존재하지_않음(조회된_지하철역_목록, 지하철역);
    }
}
