package nextstep.subway.fixtures;

import io.restassured.RestAssured;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestFixtures {

    protected String stationId1 = "";
    protected String stationId2 = "";
    protected String stationId3 = "";
    protected String stationId4 = "";
    @LocalServerPort
    int port;
    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    StationRepository stationRepository;

    @BeforeEach
    protected void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        Station station1 = stationRepository.save(new Station("경기 광주역"));
        Station station2 = stationRepository.save(new Station("중앙역"));
        Station station3 = stationRepository.save(new Station("모란역"));
        Station station4 = stationRepository.save(new Station("미금역"));
        stationId1 = String.valueOf(station1.getId());
        stationId2 = String.valueOf(station2.getId());
        stationId3 = String.valueOf(station3.getId());
        stationId4 = String.valueOf(station4.getId());
    }
}
