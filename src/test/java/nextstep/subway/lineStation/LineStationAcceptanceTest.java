package nextstep.subway.lineStation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class LineStationAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    private Station upStation;
    private Station downStation;
    private Station newUpStation;
    private Station newDownStation;
    private Station newStation;
    private Line line;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        newUpStation = stationRepository.save(new Station("신사"));
        newDownStation = stationRepository.save(new Station("광교"));
        upStation = stationRepository.save(new Station("강남"));
        downStation = stationRepository.save(new Station("광교중앙"));
        newStation = stationRepository.save(new Station("양재"));
        line = lineRepository.save(new Line("신분당선", "bg-red-600", upStation, downStation, 10L));
    }

    /**
     * When 지하철_노선에_지하철역_등록_요청
     * Then  지하철_노선에_지하철역_등록됨
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = addSection(line.getId(), new SectionRequest(upStation.getId(),
                newStation.getId(),
                5)
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getOne(line.getId()).jsonPath().getList("stations.name");
        assertThat(stationNames).containsExactlyInAnyOrder("강남", "양재", "광교중앙");
    }

    private ExtractableResponse<Response> addSection(long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getOne(Long lineId) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{lineId}", lineId)
                .then().log().all().extract();
    }

}
