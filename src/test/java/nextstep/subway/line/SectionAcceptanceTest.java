package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionAddRequest;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.StationTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 섹션 추가/삭제 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    private Line line;

    @BeforeEach
    public void setUp() {
        super.setUp();

        지하철역_추가();
        line = 라인_추가();
    }
    /**
     * Given 지하철 노선에 섹션을 추가 하면,
     * When 지하철 노선에 섹션이 추가 된다.
     * Then 지하촐 노선을 조회 하면, 추가된 섹션이 조회 된다.
     */
    @Test
    void 섹션_추가() {
        // given
        SectionAddRequest sectionAddRequest = new SectionAddRequest(StationTest.강남역.getId(), StationTest.이수역.getId(), 5L);
        // when
        ExtractableResponse<Response> expected = 섹션_추가(line, sectionAddRequest);
        // then
        expected.as(LineResponse.class).getStations().contains(new StationResponse(3L, "이수역"));
        assertThat(expected.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 지하철 노선에 섹션을 추가 하고,
     * When 삭제할 섹션을 삭제 한다.
     * Then 지하촐 노선을 조회 하면, 삭제된 섹션은 조회가 되지 않는다.
     */
    @Test
    void 섹션_삭제(){
        // given
        final SectionAddRequest sectionAddRequest = new SectionAddRequest(StationTest.강남역.getId(), StationTest.이수역.getId(), 5L);
        섹션_추가(line, sectionAddRequest);
        // when
        final ExtractableResponse<Response> expected = 섹션_삭제(line.getId(), StationTest.이수역.getId());
        // then
        assertThat(expected.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 섹션_추가(final Line line, final SectionAddRequest sectionAddRequest) {
        return RestAssured.given().log().all()
                .body(sectionAddRequest)
                .pathParam("id", line.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 섹션_삭제(final Long id, final Long stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .param("stationId", stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public void 지하철역_추가() {
        stationRepository.save(StationTest.강남역);
        stationRepository.save(StationTest.사당역);
        stationRepository.save(StationTest.이수역);
    }

    public Line 라인_추가() {
        return lineRepository.save(Line.of("2호선", "bg-blue-600", 10L, StationTest.강남역, StationTest.사당역));
    }

}
