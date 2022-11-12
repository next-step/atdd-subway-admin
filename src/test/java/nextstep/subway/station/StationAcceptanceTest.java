package nextstep.subway.station;

import io.restassured.RestAssured;
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
class StationAcceptanceTest {
    @LocalServerPort
    int port;

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
    void createStation() {
        // when
        // then
        String 지하철역 = "삼성역";
        지하철역_생성(지하철역, HttpStatus.CREATED);

        // then
        List<String> returnStationNames = 지하철목록을_조회후_지하철_역명_리스트_반환(HttpStatus.OK);
        // 첫 번째 변수는 실제 반환된 리스트, 두번째 부터는 검증할 지하철 역명을 입력한다.
        지하철_목록_검증_입력된_지하철역이_존재(returnStationNames, 지하철역);
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
        String 지하철역 = "강남역";
        지하철역_생성(지하철역, HttpStatus.CREATED);

        // when
        // then
        지하철역_생성(지하철역, HttpStatus.BAD_REQUEST);
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
        String 지하철역 = "잠실역";
        String 지하철역2 = "몽촌토성역";
        지하철역_생성(지하철역, HttpStatus.CREATED);
        지하철역_생성(지하철역2, HttpStatus.CREATED);

        // when
        List<String> returnStationNames = 지하철목록을_조회후_지하철_역명_리스트_반환(HttpStatus.OK);

        // then
        지하철_목록_검증_입력된_지하철역이_존재(returnStationNames, 지하철역, 지하철역2);
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
        String 지하철역 = "잠실역";
        Long stationId = 지하철역을_생성후_지하철_ID를_반환(지하철역, HttpStatus.CREATED);

        // when
        지하철역_제거(stationId, HttpStatus.NO_CONTENT);

        // then
        List<String> returnStationNames = 지하철목록을_조회후_지하철_역명_리스트_반환(HttpStatus.OK);
        지하철_목록_검증_입력된_지하철역이_존재하지_않음(returnStationNames, 지하철역);
    }
}
