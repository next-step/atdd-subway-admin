package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

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

    private StationResponse A역;
    private StationResponse B역;
    private StationResponse C역;
    private StationResponse D역;
    private StationResponse E역;
    private Line line;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        A역 = StationApi.지하철역_생성("A역").as(StationResponse.class);
        B역 = StationApi.지하철역_생성("B역").as(StationResponse.class);
        C역 = StationApi.지하철역_생성("C역").as(StationResponse.class);
        D역 = StationApi.지하철역_생성("D역").as(StationResponse.class);
        E역 = StationApi.지하철역_생성("E역").as(StationResponse.class);

        line = lineRepository.save(new Line("분당선", "yellow"));
    }

    /**
     * when 지하철 구간을 생성하면
     * then 지하철 구간이 생성된다.
     */
    @Test
    @DisplayName("지하철 구간을 등록한다.")
    void 지하철구간_등록_테스트() {
        // when
        LineStationRequest lineStationRequest = new LineStationRequest(A역.getId(), B역.getId(), 20L);
        ExtractableResponse<Response> response = LineStationApi.지하철구간_생성(line.getId(), lineStationRequest);

        // then
        assertThat(response.jsonPath().getLong("id")).isNotNull();
    }

    /**
     * given 분당선에 A역-C역 구간을 등록하고
     * when 분당선에 A역-B역 구간을 등록하면
     * then 분당선에 B역이 등록된다. (A역-B역-C역)
     */
    @Test
    @DisplayName("지하철 구간에 중간역 등록")
    void 지하철구간_중간역_등록() {
        //TODO: distance 검증 체크

        // given
        LineStationRequest AC_지하철구간_등록_요청 = new LineStationRequest(A역.getId(), C역.getId(), 30L);
        LineStationApi.지하철구간_생성(line.getId(), AC_지하철구간_등록_요청);

        // when
        LineStationRequest AB_지하철구간_등록_요청 = new LineStationRequest(A역.getId(), B역.getId(), 10L);
        LineStationApi.지하철구간_생성(line.getId(), AB_지하철구간_등록_요청);

        // then
        ExtractableResponse<Response> response = LineApi.지하철노선_상세_조회(line.getId());
        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);

        assertThat(stations.contains(B역)).isTrue();
    }
}
