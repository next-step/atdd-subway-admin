package nextstep.subway;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineStationRepository lineStationRepository;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    @AfterEach
    void tearDown() {
        lineRepository.deleteAll();
        lineStationRepository.deleteAll();
        stationRepository.deleteAll();
    }
}