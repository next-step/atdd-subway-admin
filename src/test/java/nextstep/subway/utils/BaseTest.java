package nextstep.subway.utils;

import io.restassured.RestAssured;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
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

    @Autowired
    public LineRepository lineRepository;

    protected static Station 서울대입구역;
    protected static Station 낙성대역;
    protected static Station 시청역;
    protected static Station 영등포역;
    protected static Station 강남역;
    protected static Station 광교역;
    protected static Station 판교역;
    protected static Line 신분당선;

    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();
        역_생성();
        노선_생성();
    }

    private void 역_생성(){
        서울대입구역 = stationRepository.save(new Station("서울대입구역"));
        낙성대역 = stationRepository.save(new Station("낙성대역"));
        시청역 = stationRepository.save(new Station("시청역"));
        영등포역 = stationRepository.save(new Station("영등포역"));
        강남역 = stationRepository.save(new Station("강남역"));
        광교역 = stationRepository.save(new Station("광교역"));
        판교역 = stationRepository.save(new Station("판교역"));
    }

    private void 노선_생성() {
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 광교역, 10));
    }
}
