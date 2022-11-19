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

    protected String 경기광주역ID = "";
    protected String 중앙역ID = "";
    protected String 모란역ID = "";
    protected String 미금역ID = "";
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
        경기광주역ID = String.valueOf(station1.getId());
        중앙역ID = String.valueOf(station2.getId());
        모란역ID = String.valueOf(station3.getId());
        미금역ID = String.valueOf(station4.getId());
    }
}
