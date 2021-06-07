package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.RestAcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends RestAcceptanceTest {

    @DisplayName("구간 추가 - 기존 구간 맨 앞에 추가")
    @Test
    void saveSection_addToFirst() {
        // given
        LineResponse lineResponse = LineAcceptanceTest.saveShinBundangLine().jsonPath().getObject(".", LineResponse.class);
        StationResponse stationResponse = StationAcceptanceTest.saveStation("동탄").jsonPath().getObject(".", StationResponse.class);

        // when
        StationResponse firstStop = lineResponse.getStations().get(0);
        StationResponse lastStop = lineResponse.getStations().get(lineResponse.getStations().size() - 1);
        saveSection(lineResponse.getId(), firstStop.getId(), stationResponse.getId(), 15);
        LineResponse expected = LineAcceptanceTest.findLine(lineResponse.getId()).jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(expected.getStations().size()).isEqualTo(3);
        assertThat(expected.getStations().get(0).getName()).isEqualTo(stationResponse.getName());
        assertThat(expected.getStations().get(expected.getStations().size() - 1).getName()).isEqualTo(lastStop.getName());
    }

    @DisplayName("구간 추가 - 기존 구간 맨 뒤에 추가")
    @Test
    void saveSection_addToLast() {
        // given
        LineResponse lineResponse = LineAcceptanceTest.saveShinBundangLine().jsonPath().getObject(".", LineResponse.class);
        StationResponse stationResponse = StationAcceptanceTest.saveStation("용산").jsonPath().getObject(".", StationResponse.class);

        // when
        StationResponse firstStop = lineResponse.getStations().get(0);
        StationResponse lastStop = lineResponse.getStations().get(lineResponse.getStations().size() - 1);
        saveSection(lineResponse.getId(), stationResponse.getId(), lastStop.getId(), 15);
        LineResponse expected = LineAcceptanceTest.findLine(lineResponse.getId()).jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(expected.getStations().size()).isEqualTo(3);
        assertThat(expected.getStations().get(0).getName()).isEqualTo(firstStop.getName());
        assertThat(expected.getStations().get(expected.getStations().size() - 1).getName()).isEqualTo(stationResponse.getName());
    }

    @DisplayName("구간 추가 - 기존 구간 중간에 추가")
    @Test
    void saveSection_addToMiddle() {
        // given
        LineResponse lineResponse = LineAcceptanceTest.saveShinBundangLine().jsonPath().getObject(".", LineResponse.class);
        StationResponse stationResponse = StationAcceptanceTest.saveStation("양재").jsonPath().getObject(".", StationResponse.class);

        // when
        StationResponse firstStop = lineResponse.getStations().get(0);
        StationResponse lastStop = lineResponse.getStations().get(lineResponse.getStations().size() - 1);
        saveSection(lineResponse.getId(), lastStop.getId(), stationResponse.getId(), 5);
        LineResponse expected = LineAcceptanceTest.findLine(lineResponse.getId()).jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(expected.getStations().size()).isEqualTo(3);
        assertThat(expected.getStations().get(0).getName()).isEqualTo(firstStop.getName());
        assertThat(expected.getStations().get(expected.getStations().size() - 1).getName()).isEqualTo(lastStop.getName());
    }

    public static ExtractableResponse<Response> saveSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", String.valueOf(distance));
        return executePost("lines/" + lineId.toString() + "/sections", params);
    }
}
