package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.util.DatabaseCleanup;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.StationTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    LineService lineService;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        지하철역_추가();
    }

    /**
     * Given 지하철 노선에 섹션을 추가 하면,
     * When 지하철 노선에 섹션이 추가 된다.
     * Then 지하촐 노선을 조회 하면, 추가된 섹션이 조회 된다.
     */
    @Test
    void 섹션_추가() {
        // given
        final Line line = lineRepository.save(Line.of("2호선", "bg-blue-600", 10L, StationTest.강남역, StationTest.사당역));
        SectionAddRequest sectionAddRequest = new SectionAddRequest(StationTest.강남역.getId(), StationTest.이수역.getId(), 5L);
        // when
        ExtractableResponse<Response> expected = 섹션_추가(line, sectionAddRequest);
        // then
        expected.as(LineResponse.class).getStations().contains(new StationResponse(3L, "이수역"));
        assertThat(expected.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    private ExtractableResponse<Response> 섹션_추가(final Line line, final SectionAddRequest sectionAddRequest) {
        ExtractableResponse<Response> expected = RestAssured.given().log().all()
                .body(sectionAddRequest)
                .pathParam("id", line.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
        return expected;
    }

    public void 지하철역_추가() {
        stationRepository.save(StationTest.강남역);
        stationRepository.save(StationTest.사당역);
        stationRepository.save(StationTest.이수역);
    }

}
