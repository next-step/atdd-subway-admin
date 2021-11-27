package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Optional;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationService stationService;

    private Long lineId;

    private Long upStationId;

    private Long downStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> 광교역_생성_응답 = 지하철_역_생성_요청("광교역");

        upStationId = 역_ID(강남역_생성_응답);
        downStationId = 역_ID(광교역_생성_응답);

        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", upStationId, downStationId, 10);

        lineId = 노선_ID(신분당선_생성_응답);
    }

    @DisplayName("지하철 노선 생성시 구간을 생성한다.")
    @Test
    void createSection() {
        // then
        지하철_구간_생성됨(upStationId, downStationId);
    }

    @DisplayName("노선에 구간 추가 성공 - 역 사이에 새로운 역을 등록한다.")
    @Test
    void addSection_success_betweenStation() {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, upStationId, 역_ID(양재역_생성_응답), 5);

        // then
        지하철_구간_추가됨(response);
    }

    @DisplayName("노선에 구간 추가 성공 - 새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSection_success_upStation() {
        // given
        ExtractableResponse<Response> 용산역_생성_응답 = 지하철_역_생성_요청("용산역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, 역_ID(용산역_생성_응답), upStationId, 5);

        // then
        지하철_구간_추가됨(response);
    }

    @DisplayName("노선에 구간 추가 성공 - 새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSection_success_downStation() {
        // given
        ExtractableResponse<Response> 호매실역_생성_응답 = 지하철_역_생성_요청("호매실역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, 역_ID(호매실역_생성_응답), upStationId, 5);

        // then
        지하철_구간_추가됨(response);
    }

    private void 지하철_구간_생성됨(long upStationId, long downStationId) {
        Optional<Section> optionalSection = 지하철_구간_조회(upStationId, downStationId);
        assertThat(optionalSection.get()).isNotNull();
    }

    private Optional<Section> 지하철_구간_조회(long upStationId, long downStationId) {
        Station upStation = stationService.findStation(upStationId);
        Station downStation = stationService.findStation(downStationId);

        return sectionRepository.findByUpStationAndDownStation(upStation, downStation);
    }

    private ExtractableResponse<Response> 지하철_구간_추가_요청(long lineId, long upStationId, long downStationId, int distance) {
        SectionRequest params = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    private void 지하철_구간_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
