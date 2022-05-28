package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.LineRestAssured.*;
import static nextstep.subway.station.StationRestAssured.지하철역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    private ExtractableResponse<Response> 지하철역_노선_등록(String upStation, String downStation, String lineName, String color) {
        Long upStationId = 지하철역_등록(upStation).jsonPath().getLong("id");
        Long downStationId = 지하철역_등록(downStation).jsonPath().getLong("id");
        return 노선_등록(lineName, color, upStationId, downStationId);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    public void createLine() {
        // when
        String lineName = "신분당선";
        ExtractableResponse<Response> response = 지하철역_노선_등록("강남역", "양재역", lineName, "bg-red-600");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lines = 노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lines).containsAnyOf(lineName);
    }

    /**
     * 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선목록을 조회한다.")
    @Test
    public void getLines() {
        // given
        지하철역_노선_등록("강남역", "양재역", "신분당선", "bg-red-600");
        지하철역_노선_등록("선릉역", "한티역", "분당선", "bg-yellow-600");

        // when
        List<String> lineNames = 노선_목록_조회().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    public void getLine() {
        // given
        String lineName = "신분당선";
        ExtractableResponse<Response> response = 지하철역_노선_등록("강남역", "양재역", lineName, "bg-red-600");
        Long lineId = response.jsonPath().getLong("id");

        // when
        String result = 노선_조회(lineId).jsonPath().getString("name");

        // then
        assertThat(result).isEqualTo(lineName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("노선을 수정한다.")
    @Test
    public void updateLine() {
        // given
        ExtractableResponse<Response> response = 지하철역_노선_등록("강남역", "양재역", "신분당선", "bg-red-600");
        Long lineId = response.jsonPath().getLong("id");

        // when
        String updatedLineName = "분당선";
        노선_수정(lineId, updatedLineName, "bg-yellow-600");


        // then
        String result = 노선_조회(lineId).jsonPath().getString("name");
        assertThat(result).isEqualTo(updatedLineName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("노선을 삭제한다.")
    @Test
    public void deleteLine() {
        // given
        String lineName = "신분당선";
        ExtractableResponse<Response> response = 지하철역_노선_등록("강남역", "양재역", lineName, "bg-red-600");
        Long lineId = response.jsonPath().getLong("id");

        // when
        노선_삭제(lineId);

        // then
        List<String> lines = 노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lines).doesNotContain(lineName);
    }
}
