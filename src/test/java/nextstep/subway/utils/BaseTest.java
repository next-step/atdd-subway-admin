package nextstep.subway.utils;

import io.restassured.RestAssured;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {
    @LocalServerPort
    public int port;

    @Autowired
    public DatabaseCleanup databaseCleanup;

    @Autowired
    StationRepository stationRepository;

    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();
        역_생성();
    }

    private void 역_생성(){
        stationRepository.save(new Station("서울대입구역"));
        stationRepository.save(new Station("낙성대역"));
        stationRepository.save(new Station("시청역"));
        stationRepository.save(new Station("영등포역"));
    }
}
