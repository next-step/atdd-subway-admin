package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private static final String STATIONS = "stations";
    private static final String SECTION_MAIN_PATH = LINE_MAIN_PATH + "/%d" + "/sections";
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Station otherUpStation;
    private Station otherDownStation;

    @BeforeEach
    void init() {
        upStation = stationRepository.save(new Station("강남역"));
        downStation = stationRepository.save(new Station("성수역"));
        otherUpStation = stationRepository.save(new Station("홍대역"));
        otherDownStation = stationRepository.save(new Station("구의역"));
        line = new Line("테스트노선", "red");
        line.addStations(upStation, downStation, 10L);
        lineRepository.save(line);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), downStation.getId(), otherDownStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findById(line.getId());
        List<StationResponse> stations = convertStationsName(findResponse.jsonPath());
        assertThat(stations).hasSize(3);

    }

    @DisplayName("노선 구간에 새로운 상행 종점을 추가 할 수 있다.")
    @Test
    void addUpStation() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), otherUpStation.getId(), upStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findById(line.getId());
        List<StationResponse> stations = convertStationsName(findResponse.jsonPath());
        assertThat(stations).hasSize(3);
        assertThat(stations.get(0).getId()).isEqualTo(otherUpStation.getId());

    }

    @DisplayName("노선 구간에 새로운 하행 종점을 추가 할 수 있다.")
    @Test
    void addDownStation() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), downStation.getId(), otherUpStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findById(line.getId());
        List<StationResponse> stations = convertStationsName(findResponse.jsonPath());
        assertThat(stations).hasSize(3);
        assertThat(stations.get(stations.size() - 1).getId()).isEqualTo(otherUpStation.getId());

    }

    @DisplayName("중복된 노선 구간은 저장할 수 없다.")
    @Test
    void addSectionFailedDuplicate() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), upStation.getId(), downStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }


    private ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put(UP_STATION, upStationId);
        params.put(DOWN_STATION, downStationId);
        params.put(DISTANCE, distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(String.format(SECTION_MAIN_PATH, lineId))
                .then().log().all()
                .extract();
    }

    private List<StationResponse> convertStationsName(JsonPath jsonPath) {
        return jsonPath.getList(STATIONS, StationResponse.class);
    }

}
