package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseTest;
import nextstep.subway.domain.line.*;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.request.LineSectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 등록 테스트")
public class SectionAcceptanceTest extends BaseTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    public void setUp() {
        Station 강남역 = 지하철역_생성("강남역");
        Station 서초역 = 지하철역_생성("서초역");

        List<LineStation> lineStation = Stream.of(new LineStation(강남역.getId(), 서초역.getId(), 7))
                                        .collect(Collectors.toList());
        지하철역_라인_생성("신분당선", "red", new LineStations(lineStation));
    }

    private Station 지하철역_생성(String stationName) {
        return stationRepository.save(new Station(stationName));
    }

    private Line 지하철역_라인_생성(String lineName, String color, LineStations lineStations) {
        return lineRepository.save(new Line(lineName, color, lineStations));
    }

    /**
     * 상행, 하행 지하철역을 생성하고 구간을 설정한후,
     * 기존 노선 사이에 새로운 역 등록을 요청하면
     * 기존 노선에 등록된다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록한다")
    public void addSectionsTest() {
        // Given
        Station firstStation = stationRepository.getByName("강남역");
        Station secondStation = 지하철역_생성("마포구청역");

        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 5);

        // When
        List<HashMap> stations = request_register_line_section(lineSectionRequest).jsonPath().get("stations");

        // Then
        assertThat(구간_지하철역_추출(stations)).contains(Long.valueOf(firstStation.getId()), Long.valueOf(secondStation.getId()));
    }

    private List<Long> 구간_지하철역_추출(List<HashMap> stations) {
        return stations.stream()
                .map(it -> Long.valueOf((Integer) it.get("upStationId")))
                .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> request_register_line_section(LineSectionRequest lineSectionRequest) {
        Line line = lineRepository.getByName("신분당선");
        return RestAssured.given().log().all()
                .pathParam("id", line.getId())
                .body(lineSectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    /**
     * 상행, 하행 지하철역을 생성하고 구간을 설정한후,
     * 새로운 역을 상행종점으로 등록을 요청하면
     * 기존 노선에 등록된다.
     */
    @Test
    @DisplayName("새로운역을 상행종점으로 등록한다")
    public void addFirstSectionsTest() {
        // Given
        Station firstStation = 지하철역_생성("마포구청역");
        Station secondStation = stationRepository.getByName("강남역");

        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 5);

        // When
        List<HashMap> stations = request_register_line_section(lineSectionRequest).jsonPath().get("stations");

        // Then
        assertThat(구간_지하철역_추출(stations)).contains(Long.valueOf(firstStation.getId()), Long.valueOf(secondStation.getId()));
    }

    /**
     * 상행, 하행 지하철역을 생성하고 구간을 설정한후,
     * 새로운 역을 하행종점으로 등록을 요청하면
     * 기존 노선에 등록된다.
     */
    @Test
    @DisplayName("새로운역을 하행종점으로 등록한다")
    public void addLastSectionsTest() {
        // Given
        Station firstStation = stationRepository.getByName("강남역");
        Station secondStation = stationRepository.getByName("서초역");
        Station thirdStation = 지하철역_생성("마포구청역");

        LineSectionRequest lineSectionRequest= new LineSectionRequest(secondStation.getId(), thirdStation.getId(), 5);

        // When
        List<HashMap> stations = request_register_line_section(lineSectionRequest).jsonPath().get("stations");

        // Then
        assertThat(구간_지하철역_추출(stations)).contains(Long.valueOf(firstStation.getId()), Long.valueOf(secondStation.getId()));
    }

    /**
     * 상행, 하행 지하철역이
     * 모두 노선에 등록되어 있다면
     * 기존 노선에 등록할수 없다.
     */
    @Test
    @DisplayName("등록하려는 지하철노선이 기존 노선에 등록되어 있어, 노선 등록이 실패하는 테스트")
    public void addSectionsFailTestForExistStation() {
        // Given
        Station firstStation = stationRepository.getByName("강남역");
        Station secondStation = stationRepository.getByName("서초역");

        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 7);

        // When
        ExtractableResponse<Response> response = request_register_line_section(lineSectionRequest);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 추가하려는 상행, 하행 지하철역의 길이가
     * 추가되는 노선 사이의 길이보다 길다면
     * 기존 노선에 등록할수 없다.
     */
    @Test
    @DisplayName("등록하려는 지하철노선 길이가 기존 노선길이보다 길다면, 노선 등록이 실패하는 테스트")
    public void addSectionsFailTestForDistance() {
        // Given
        Station firstStation = stationRepository.getByName("강남역");
        Station secondStation = stationRepository.save(new Station("마포구청역"));

        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 12);

        // When
        ExtractableResponse<Response> response = request_register_line_section(lineSectionRequest);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 추가하려는 상행, 하행역이
     * 기존 노선에 포함되어 있지 않다면
     * 기존 노선에 등록할수 없다.
     */
    @Test
    @DisplayName("등록하려는 지하철노선역이 기존노선에 포함되어 있지 않다면, 노선 등록이 실패하는 테스트")
    public void addSectionsFailTestForNotExistStation() {
        // Given
        Station firstStation = stationRepository.save(new Station("망원역"));
        Station secondStation = stationRepository.save(new Station("마포구청역"));

        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 5);

        // When
        ExtractableResponse<Response> response = request_register_line_section(lineSectionRequest);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
