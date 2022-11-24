package nextstep.subway.section;

import io.restassured.RestAssured;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    private static final String BASE_URL = "/lines";

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선에 신규로 구간을 등록하면
     * Then 등록한 구간이 조회된다.
     */
    @DisplayName("노선에 첫 신규 구간을 등록한다.")
    @Test
    void 지하철_노선_첫_구간_생성_테스트() {

    }

    /**
     * given 기존의 구간에
     * when 새로운 지하철 역을 추가하면
     * then 새로운 구간이 생성된다.
     */
    @DisplayName("구간에 새로운 역을 등록한다.")
    @Test
    void 지하철_구간에_새로운_역_생성_테스트() {

    }

    /**
     * Given 지하철 구간을 2번 등록하고
     * When 지하철 노선 전체 구간을 조회하면
     * Then 등록한 구간들이 조회된다.
     */
    @DisplayName("노선의 전체 구간을 조회한다.")
    @Test
    void 지하철_노선_전체_구간_조회_테스트() {

    }

    /**
     * Given 기존의 상행 종점역에
     * When 새로운 상행 종점역 생성 요청을 하면
     * Then 지하철 노선에 새로운 상행 종점역이 등록된다.
     */
    @DisplayName("새로운 상행 종점역을 생성한다.")
    @Test
    void 새로운_상행_종점역_생성_테스트() {

    }

    /**
     * Given 기존의 하행 종점역에
     * When 새로운 하행 종점역 생성 요청을 하면
     * Then 지하철 노선에 새로운 하행 종점역이 등록된다.
     */
    @DisplayName("새로운 하행 종점역을 생성한다.")
    @Test
    void 새로운_하행_종점역_생성_테스트() {

    }

    /**
     * Given 기존 구간에
     * When 기존 구간 거리 이상의 거리를 가진 새로운 역을 등록 하면
     * Then 등록이 거부된다.
     */
    @DisplayName("기존 구간 이상 거리의 새로운 역을 등록한다.")
    @Test
    void 기존_구간_거리_이상_거리의_역_추가_테스트() {

    }

    /**
     * Given 기존 구간에 등록된
     * When 상행역과 하행역을 가진 구간을 등록하면
     * Then 등록이 거부된다.
     */
    @DisplayName("기존에 등록된 상행역과 하행역의 구간을 등록한다.")
    @Test
    void 기존에_등록된_상하행역_등록_테스트() {

    }

    /**
     * Given 기존 구간에 등록되지 않은
     * When 상행역과 하행역을 가진 구간을 등록하면
     * Then 등록이 거부된다.
     */
    @DisplayName("기존에 등록되지 않은 상행역과 하행역의 구간을 등록한다.")
    @Test
    void 기존에_등록되지_않은_상하행역_등록_테스트() {

    }

}