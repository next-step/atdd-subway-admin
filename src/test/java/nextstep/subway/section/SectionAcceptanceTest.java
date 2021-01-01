package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static nextstep.subway.line.LineAcceptanceTest.DEFAULT_LINES_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    public static final String DEFAULT_SECTIONS_URI = "/sections";

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    private Station hwagokStation;
    private Station kkachisanStation;
    private Station gimpoAirportStation;
    private Station mokdongStation;

    private Line lineNumber5;

    @BeforeEach
    void sectionTestSetUp() {
        super.setUp();

        // given
        지하철역_생성();
        노선_생성();
    }

    void 지하철역_생성() {
        hwagokStation = new Station("화곡역");
        kkachisanStation = new Station("까치산역");
        gimpoAirportStation = new Station("김포공항역");
        mokdongStation = new Station("목동역");

        stationRepository.save(hwagokStation);
        stationRepository.save(kkachisanStation);
        stationRepository.save(gimpoAirportStation);
        stationRepository.save(mokdongStation);
    }

    void 노선_생성() {
        lineNumber5 = new Line("5호선", "보라색", gimpoAirportStation, kkachisanStation, 20L);

        lineRepository.save(lineNumber5);
    }

    @DisplayName("노선에 이미 존재하는 구간에 하행역과 동일한 상행역을 포함한 새 구간을 등록한다.")
    @Test
    void addSectionWhenExistDownStationEqualsUpStationInNewSection() {
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", kkachisanStation.getId() + "");
        params.put("downStationId", mokdongStation.getId() + "");
        params.put("distance", 10L + "");

        final ExtractableResponse<Response> addSectionResponse = 구간_등록_요청(params, lineNumber5.getId());
        final LineResponse lineAfterAddSection = addSectionResponse.as(LineResponse.class);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertAll(
            () -> assertThat(lineAfterAddSection).isNotNull(),
            () -> assertThat(lineAfterAddSection.getName()).isEqualTo("5호선"),
            () -> assertThat(lineAfterAddSection.getColor()).isEqualTo("보라색"),
            () -> assertThat(lineAfterAddSection.getStations().stream().map(StationResponse::getName).collect(toList()))
                .containsOnly("김포공항역", "까치산역", "목동역")
        );
    }

    private ExtractableResponse<Response> 구간_등록_요청(final Map<String, String> params, final long lineId) {
        return POST_요청_보내기(params, DEFAULT_LINES_URI + "/" + lineId + DEFAULT_SECTIONS_URI);
    }

    @DisplayName("노선에 이미 존재하는 구간에 상행역과 동일한 하행역을 포함한 새 구간을 등록한다.")
    @Test
    void addSectionWhenExistUpStationEqualsDownStationInNewSection() {
        // 김포공항 - 까치산
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", mokdongStation.getId() + "");
        params.put("downStationId", gimpoAirportStation.getId() + "");
        params.put("distance", 10L + "");

        final ExtractableResponse<Response> addSectionResponse = 구간_등록_요청(params, lineNumber5.getId());
        final LineResponse lineAfterAddSection = addSectionResponse.as(LineResponse.class);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertAll(
            () -> assertThat(lineAfterAddSection).isNotNull(),
            () -> assertThat(lineAfterAddSection.getName()).isEqualTo("5호선"),
            () -> assertThat(lineAfterAddSection.getColor()).isEqualTo("보라색"),
            () -> assertThat(lineAfterAddSection.getStations().get(0).getName()).isEqualTo("목동역"),
            () -> assertThat(lineAfterAddSection.getStations().get(1).getName()).isEqualTo("김포공항역"),
            () -> assertThat(lineAfterAddSection.getStations().get(2).getName()).isEqualTo("까치산역")
        );
    }

    @DisplayName("노선에 이미 존재하는 구간에 상행역과 상행역이 동일한 구간을 등록한다.")
    @Test
    void addSectionWhenExistUpStationEqualsUpStationInNewSection() {
        // 김포공항 - 까치산
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", gimpoAirportStation.getId() + "");
        params.put("downStationId", mokdongStation.getId() + "");
        params.put("distance", 10L + "");

        final ExtractableResponse<Response> addSectionResponse = 구간_등록_요청(params, lineNumber5.getId());
        final LineResponse lineAfterAddSection = addSectionResponse.as(LineResponse.class);

        assertAll(
            () -> assertThat(lineAfterAddSection).isNotNull(),
            () -> assertThat(lineAfterAddSection.getName()).isEqualTo("5호선"),
            () -> assertThat(lineAfterAddSection.getColor()).isEqualTo("보라색"),
            () -> assertThat(lineAfterAddSection.getStations().get(0).getName()).isEqualTo("김포공항역"),
            () -> assertThat(lineAfterAddSection.getStations().get(1).getName()).isEqualTo("목동역"),
            () -> assertThat(lineAfterAddSection.getStations().get(2).getName()).isEqualTo("까치산역")
        );
    }

    @DisplayName("노선에 이미 존재하는 구간에 하행역과 하행역이 동일한 구간을 등록한다.")
    @Test
    void addSectionWhenExistDownStationEqualsDownStationInNewSection() {
        // 김포공항 - 까치산
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", hwagokStation.getId() + "");
        params.put("downStationId", kkachisanStation.getId() + "");
        params.put("distance", 10L + "");

        final ExtractableResponse<Response> addSectionResponse = 구간_등록_요청(params, lineNumber5.getId());
        final LineResponse lineAfterAddSection = addSectionResponse.as(LineResponse.class);

        assertAll(
            () -> assertThat(lineAfterAddSection).isNotNull(),
            () -> assertThat(lineAfterAddSection.getName()).isEqualTo("5호선"),
            () -> assertThat(lineAfterAddSection.getColor()).isEqualTo("보라색"),
            () -> assertThat(lineAfterAddSection.getStations().get(0).getName()).isEqualTo("김포공항역"),
            () -> assertThat(lineAfterAddSection.getStations().get(1).getName()).isEqualTo("화곡역"),
            () -> assertThat(lineAfterAddSection.getStations().get(2).getName()).isEqualTo("까치산역")
        );
    }

    @DisplayName("노선에 이미 존재하는 구간에 상행역과 하행역이 모두 존재하는 구간을 등록한다.")
    @Test
    void addSectionWhenExistUpAndDownStation() {
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", gimpoAirportStation.getId() + "");
        params.put("downStationId", kkachisanStation.getId() + "");
        params.put("distance", 10L + "");

        final ExtractableResponse<Response> response = 구간_등록_요청(params, lineNumber5.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("노선에 이미 존재하는 구간에 상행역과 하행역이 모두 없는 구간을 등록한다.")
    @Test
    void addSectionWhenNotExistUpAndDownStation() {
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", hwagokStation.getId() + "");
        params.put("downStationId", mokdongStation.getId() + "");
        params.put("distance", 10L + "");

        final ExtractableResponse<Response> response = 구간_등록_요청(params, lineNumber5.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
