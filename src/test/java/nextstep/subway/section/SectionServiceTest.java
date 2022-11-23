package nextstep.subway.section;

import io.restassured.RestAssured;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionLineStationService;
import nextstep.subway.application.SectionStationService;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    SectionStationService sectionStationService;

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

        upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("강남역"));
        downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("광교역"));

        lineId = Long.parseLong(LineAcceptanceTest.createLineAndGetId(
                new LineRequest("신분당선","bg-red-600",upStationId,downStationId,100)));
    }

    @AfterEach
    public void cleanUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 구간에 이미 등록되어 있는 경우 오류 발생")
    void upStationDownStationAlreadyExistTest() {
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(upStationId,downStationId,10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 역방향으로 모두 구간에 이미 등록되어 있는 경우 오류 발생")
    void upStationDownStationReverseAlreadyExistTest() {
        assertThatThrownBy(() -> sectionLineStationService
                .addSection(lineId, new SectionRequest(downStationId,upStationId,10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
