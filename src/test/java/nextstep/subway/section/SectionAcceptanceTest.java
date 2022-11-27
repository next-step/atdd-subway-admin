package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseTest;
import nextstep.subway.domain.line.*;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.request.LineSectionRequest;
import nextstep.subway.dto.response.LineStationResponse;
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
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("구간 등록 테스트")
public class SectionAcceptanceTest extends BaseTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineStationRepository lineStationRepository;

    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    public void setUp() {
        Station gangNam = stationRepository.save(new Station("강남역"));
        Station seocho = stationRepository.save(new Station("서초역"));

        List<LineStation> lineStation = Stream.of(new LineStation(gangNam.getId(), seocho.getId(), 7))
                                        .collect(Collectors.toList());
        LineStations lineStations = new LineStations(lineStation);
        Line line = new Line("신분당선", "red", lineStations);
        lineRepository.save(line);
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
        Station secondStation = stationRepository.save(new Station("마포구청역"));
        Station thirdStation = stationRepository.getByName("서초역");

        LineStation existLineStation = lineStationRepository.findByUpStationIdAndDownStationId(firstStation.getId(), thirdStation.getId());
        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 5);

        // When
        List<HashMap> stations = request_register_line_section(lineSectionRequest).jsonPath().get("stations");
        LineStationResponse firstLineSection = makeTestLineStationResponse(stations.get(0));
        LineStationResponse secondLineSection = makeTestLineStationResponse(stations.get(1));

        // Then
        assertAll(
                () -> assertEquals(firstLineSection.getUpStationId(), secondStation.getId()),
                () -> assertEquals(firstLineSection.getDownStationId(), thirdStation.getId()),
                () -> assertEquals(firstLineSection.getDistance(), existLineStation.getDistance() - lineSectionRequest.getDistance()),

                () -> assertEquals(secondLineSection.getUpStationId(), firstStation.getId()),
                () -> assertEquals(secondLineSection.getDownStationId(), secondStation.getId()),
                () -> assertEquals(secondLineSection.getDistance(), lineSectionRequest.getDistance())
        );
    }

    private LineStationResponse makeTestLineStationResponse(HashMap<String, Integer> station) {
        return new LineStationResponse(
                                      Long.valueOf(station.get("upStationId"))
                                     ,Long.valueOf(station.get("downStationId"))
                                     ,station.get("distance")
                                    );
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
        Station firstStation = stationRepository.save(new Station("마포구청역"));
        Station secondStation = stationRepository.getByName("강남역");
        Station thirdStation = stationRepository.getByName("서초역");

        LineStation existLineStation = lineStationRepository.findByUpStationIdAndDownStationId(secondStation.getId(), thirdStation.getId());
        LineSectionRequest lineSectionRequest= new LineSectionRequest(firstStation.getId(), secondStation.getId(), 5);

        // When
        List<HashMap> stations = request_register_line_section(lineSectionRequest).jsonPath().get("stations");
        LineStationResponse firstLineSection = makeTestLineStationResponse(stations.get(0));
        LineStationResponse secondLineSection = makeTestLineStationResponse(stations.get(1));

        // Then
        assertAll(
                () -> assertEquals(firstLineSection.getUpStationId(), secondStation.getId()),
                () -> assertEquals(firstLineSection.getDownStationId(), thirdStation.getId()),
                () -> assertEquals(firstLineSection.getDistance(), existLineStation.getDistance()),

                () -> assertEquals(secondLineSection.getUpStationId(), firstStation.getId()),
                () -> assertEquals(secondLineSection.getDownStationId(), secondStation.getId()),
                () -> assertEquals(secondLineSection.getDistance(), lineSectionRequest.getDistance())
        );
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
        Station thirdStation = stationRepository.save(new Station("마포구청역"));

        LineStation existLineStation = lineStationRepository.findByUpStationIdAndDownStationId(firstStation.getId(), secondStation.getId());
        LineSectionRequest lineSectionRequest= new LineSectionRequest(secondStation.getId(), thirdStation.getId(), 5);

        // When
        List<HashMap> stations = request_register_line_section(lineSectionRequest).jsonPath().get("stations");
        LineStationResponse firstLineSection = makeTestLineStationResponse(stations.get(0));
        LineStationResponse secondLineSection = makeTestLineStationResponse(stations.get(1));

        // Then
        assertAll(
                () -> assertEquals(firstLineSection.getUpStationId(), firstStation.getId()),
                () -> assertEquals(firstLineSection.getDownStationId(), secondStation.getId()),
                () -> assertEquals(firstLineSection.getDistance(), existLineStation.getDistance()),

                () -> assertEquals(secondLineSection.getUpStationId(), secondStation.getId()),
                () -> assertEquals(secondLineSection.getDownStationId(), thirdStation.getId()),
                () -> assertEquals(secondLineSection.getDistance(), lineSectionRequest.getDistance())
        );
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
