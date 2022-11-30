package nextstep.subway;

import io.restassured.RestAssured;
import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    protected static final String DELIMITER = "/";

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    StationService stationService;

    @Autowired
    LineService lineService;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
    }
    @Transactional
    public long creatStation(String name) {
        return stationService.saveStation(new StationRequest(name)).getId();
    }

    @Transactional
    public void createLine(String name, String color, long upStationId, long downStationId, long distance) {
        lineService.saveLine(new LineRequest(name, color, upStationId, downStationId, distance));
    }

    public Line findLine(String name) {
        return lineService.findLine(name);
    }

    public Station findStation(String name) {
        return stationService.findStation(name);
    }

    public StationRequest generateStationRequest(String name) {
        return new StationRequest(name);
    }

    public LineRequest generateLineRequest(String name, String color) {
        return new LineRequest(name, color, 1, 2, 1);
    }

    public LineRequest generateLineRequest(String name, String color, long upStationId, long downStationId, long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
}
