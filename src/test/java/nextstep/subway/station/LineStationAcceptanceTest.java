package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineStationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 구간 관련기능 테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineStationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        upStation = stationRepository.save(new Station("왕십리역"));
        downStation = stationRepository.save(new Station("수원역"));
        line = lineRepository.save(new Line("분당선", "yellow"));
    }

    @Test
    @DisplayName("지하철 구간을 등록한다.")
    void 지하철구간_등록_테스트() {
        // given
        upStation = stationRepository.save(new Station("이매역"));
        downStation = stationRepository.save(new Station("서현역"));

        LineStationRequest lineStationRequest = new LineStationRequest(upStation.getId(), downStation.getId(), 20L);

        // when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(lineStationRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/line/" + line.getId() + "/section")
                        .then().log().all()
                        .extract();

        ApiAcceptance.API응답_검증(response.statusCode(), HttpStatus.CREATED.value());

        // then
        assertThat(response.jsonPath().getLong("id")).isNotNull();
    }
}
