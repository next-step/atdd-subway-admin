package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.LineRestAssured.노선_등록;
import static nextstep.subway.line.LineRestAssured.노선_조회;
import static nextstep.subway.station.StationRestAssured.지하철역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    public void createLine() {
        // when
        Long upStationId = 지하철역_등록("강남역").jsonPath().getLong("id");
        Long downStationId = 지하철역_등록("양재역").jsonPath().getLong("id");
        String lineName = "신분당선";
        ExtractableResponse<Response> response = 노선_등록(lineName, "bg-red-600", upStationId, downStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lines = 노선_조회().jsonPath().getList("name", String.class);
        assertThat(lines).containsAnyOf(lineName);
    }
}
