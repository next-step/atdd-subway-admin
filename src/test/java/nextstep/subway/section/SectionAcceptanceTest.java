package nextstep.subway.section;

import io.restassured.RestAssured;
import nextstep.subway.testutils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 상행종점역, 새로운역 구간을 추가하고 노선을 조회하면
     * Then 상행종점역, 새로운역, 하행종점역이 조회된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionBetweenStations() {
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 새로운역, 상행종점역 구간을 추가하고 노선을 조회하면
     * Then 새로운역, 상행종점역, 하행종점역이 조회된다
     */
    @DisplayName("새로운 역을 가장 앞에 등록한다.")
    @Test
    void createSectionBeforeFirstStation() {
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 하행종점역, 새로운역 구간을 추가하고 노선을 조회하면
     * Then 상행좀점역, 하행종점역, 새로운역이 조회된다
     */
    @DisplayName("새로운 역을 가장 뒤에 등록한다.")
    @Test
    void createSectionAfterLastStation() {
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 새로운역, 하행종점역을 기존 길이보다 길거나 같게 등록하면
     * Then 예외가 발생한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void exception_createLongerSectionBetweenStations() {
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 상행종점역, 하행종점역 구간을 추가하면
     * Then 예외가 발생한다
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void exception_createSectionWithAlreadyExistingStations() {
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 새로운역, 새로운역2 구간을 추가하면
     * Then 예외가 발생한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void exception_createSectionWithUnknownStations() {
    }
}
