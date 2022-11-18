package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 구간 관련 기능")
public class LineSectionAcceptanceTest extends LineAcceptanceTest {
    private static final String STATIONS = "stations";
    private static final String SECTION_MAIN_PATH = LINE_MAIN_PATH + "/%d" + "/sections";
    @Autowired
    private LineRepository lineRepository;
    private Line line;

    @BeforeEach
    void init() {
        super.init();
        line = new Line("분당선", "red");
        line.addStations(upStation, downStation, 10L);
        lineRepository.save(line);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //when
        ExtractableResponse<Response> saveResponse = createSection(line.getId(), upStation.getId(), otherDownStation.getId(), 8L);

        //then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> findResponse = findById(line.getId());
        List<StationResponse> stations = convertStationsName(findResponse.jsonPath());
        assertThat(stations).hasSize(3);

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
