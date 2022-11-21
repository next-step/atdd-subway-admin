package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.request.LineApi;
import nextstep.subway.station.request.SectionApi;
import nextstep.subway.station.request.StationApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {
    /**
     * Given 지하철역과 노선이 등록되어 있고
     * When 지하철 노선에 새로운 역을 구간으로 등록하면
     * Then 지하철 노선 목록에서 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록한다")
    void addLineStation() {
        Long gangnamStationId = getId(StationApi.createStation("강남역"));
        Long seongsuStationId = getId(StationApi.createStation("성수역"));

        Long lineId = getId(LineApi.createStationLine("신분당선", gangnamStationId, seongsuStationId, 10));

        Long pangyoStationId = getId(StationApi.createStation("판교역"));

        int addSectionStatusCode = SectionApi.requestAddSection(lineId, gangnamStationId, pangyoStationId, 7).statusCode();
        assertThat(addSectionStatusCode).isEqualTo(200);

        ExtractableResponse<Response> response = LineApi.getStationLine(lineId);
        List<String> stationNames = response.jsonPath().getList("stations.name");
        assertThat(stationNames).contains("강남역", "판교역", "성수역");
    }

    /**
     * Given 지하철역과 노선이 등록되어 있고
     * When 지하철 노선에 새로운 역을 상행 종점 구간으로 등록하면
     * Then 지하철 노선 목록에서 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록한다")
    void addLineStation_new_upStation() {
        Long gangnamStationId = getId(StationApi.createStation("강남역"));
        Long seongsuStationId = getId(StationApi.createStation("성수역"));

        Long lineId = getId(LineApi.createStationLine("신분당선", gangnamStationId, seongsuStationId, 10));

        Long pangyoStationId = getId(StationApi.createStation("판교역"));

        int addSectionStatusCode = SectionApi.requestAddSection(lineId, pangyoStationId, gangnamStationId, 7).statusCode();
        assertThat(addSectionStatusCode).isEqualTo(200);

        ExtractableResponse<Response> response = LineApi.getStationLine(lineId);
        List<String> stationNames = response.jsonPath().getList("stations.name");
        assertThat(stationNames).contains("판교역", "강남역", "성수역");
    }

    /**
     * Given 지하철역과 노선이 등록되어 있고
     * When 지하철 노선에 새로운 역을 하행 종점 구간으로 등록하면
     * Then 지하철 노선 목록에서 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록한다")
    void addLineStation_new_downStation() {
        Long gangnamStationId = getId(StationApi.createStation("강남역"));
        Long seongsuStationId = getId(StationApi.createStation("성수역"));

        Long lineId = getId(LineApi.createStationLine("신분당선", gangnamStationId, seongsuStationId, 10));

        Long pangyoStationId = getId(StationApi.createStation("판교역"));

        int addSectionStatusCode = SectionApi.requestAddSection(lineId, seongsuStationId, pangyoStationId, 7).statusCode();
        assertThat(addSectionStatusCode).isEqualTo(200);

        ExtractableResponse<Response> response = LineApi.getStationLine(lineId);
        List<String> stationNames = response.jsonPath().getList("stations.name");
        assertThat(stationNames).contains("강남역", "성수역", "판교역");
    }

    /**
     * Given 지하철역과 노선이 등록되어 있고
     * When 지하철 노선에 기존 역 사이 구간보다 긴 거리로 새로운 역을 등록하면
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이 길이보다 길거나 같으면 등록할 수 없다")
    void addLineStation_When_BigDistance_IllegalArgument() {
        Long gangnamStationId = getId(StationApi.createStation("강남역"));
        Long seongsuStationId = getId(StationApi.createStation("성수역"));

        Long lineId = getId(LineApi.createStationLine("신분당선", gangnamStationId, seongsuStationId, 10));

        Long pangyoStationId = getId(StationApi.createStation("판교역"));

        ExtractableResponse<Response> response = SectionApi.requestAddSection(lineId, gangnamStationId, pangyoStationId, 10);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("추가할 구간의 거리가 기존 역 사이 거리보다 길 수는 없습니다.");
    }

    /**
     * Given 지하철역과 노선이 등록되어 있고
     * When 지하철 노선에 이미 모두 등록된 역을 등록하면
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    void addLineStation_When_containsAllSection_IllegalArgument() {
        Long gangnamStationId = getId(StationApi.createStation("강남역"));
        Long seongsuStationId = getId(StationApi.createStation("성수역"));

        Long lineId = getId(LineApi.createStationLine("신분당선", gangnamStationId, seongsuStationId, 10));

        Long pangyoStationId = getId(StationApi.createStation("판교역"));

        ExtractableResponse<Response> response = SectionApi.requestAddSection(lineId, gangnamStationId, seongsuStationId, 5);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("상행역과 하행역이 이미 노선에 모두 등록되어 있어 추가할 수 없습니다.");
    }

    /**
     * Given 지하철역과 노선이 등록되어 있고
     * When 지하철 노선에 등록되지 않은 역을 등록하면
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다")
    void addLineStation_When_noneContainsAllSection_IllegalArgument() {
        Long gangnamStationId = getId(StationApi.createStation("강남역"));
        Long seongsuStationId = getId(StationApi.createStation("성수역"));

        Long lineId = getId(LineApi.createStationLine("신분당선", gangnamStationId, seongsuStationId, 10));

        Long pangyoStationId = getId(StationApi.createStation("판교역"));
        Long moranStationId = getId(StationApi.createStation("모란역"));

        ExtractableResponse<Response> response = SectionApi.requestAddSection(lineId, pangyoStationId, moranStationId, 5);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("상행역과 하행역 둘 중 하나도 포함되어있지 않아 추가할 수 없습니다.");
    }
}
