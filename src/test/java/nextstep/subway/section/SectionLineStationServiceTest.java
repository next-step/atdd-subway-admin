package nextstep.subway.section;

import io.restassured.RestAssured;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionLineStationService;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionLineStationServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    SectionLineStationService sectionLineStationService;

    @Autowired
    LineService lineService;

    @Autowired
    StationService stationService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static long upStationId;
    private static long downStationId;
    private static long lineId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        upStationId = StationAcceptanceTest.createStationAndGetId("강남역");
        downStationId = StationAcceptanceTest.createStationAndGetId("광교역");

        lineId = Long.parseLong(LineAcceptanceTest.createLineAndGetId(
                new LineRequest("신분당선","bg-red-600",upStationId,downStationId,100)));
    }

    @AfterEach
    public void cleanUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 구간에 이미 등록되어 있는 경우 오류 발생 테스트")
    void upStationDownStationAlreadyExistExceptionTest() {
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(upStationId,downStationId,10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 역방향으로 모두 구간에 이미 등록되어 있는 경우 오류 발생 테스트")
    void upStationDownStationReverseAlreadyExistExceptionTest() {
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(downStationId,upStationId,10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역, 하행역 둘 중 하나도 포함되어 있지 않은 경우 오류 발생 테스트")
    void upStationDownStationNotExistExceptionTest() {
        //given
        Long newUpStationId = StationAcceptanceTest.createStationAndGetId("새로운역");
        Long newDownStationId = StationAcceptanceTest.createStationAndGetId("또다른새역");
        //when
        //then
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(newUpStationId, newDownStationId, 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역사이 길이보다 크거나 같으면 오류 발생 테스트")
    void exceedDistanceExceptionTest() {
        //given
        Long newUpStationId = StationAcceptanceTest.createStationAndGetId("새로운역");
        //when
        //then
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(newUpStationId, downStationId, 100)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(newUpStationId, downStationId, 101)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("섹션구간 길이 값이 음수거나 0인 경우 오류 발생 테스트")
    void invalidSectionDistanceExceptionTest() {
        //given
        Long newUpStationId = StationAcceptanceTest.createStationAndGetId("새로운역");
        //when
        //then
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(newUpStationId, downStationId, 0)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(newUpStationId, downStationId, -10)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
