package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends BaseAcceptanceTest {
    @LocalServerPort
    int port;

    private LineRequest firstLine;
    private LineRequest secondLine;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        setDefaultLine();
    }

    private void setDefaultLine() {
        StationResponse firstLineUpStation = createStationRequest("강남역")
                .jsonPath().getObject("", StationResponse.class);
        StationResponse firstLineDownStation = createStationRequest("광교역")
                .jsonPath().getObject("", StationResponse.class);

        StationResponse secondLineUpStation = createStationRequest("대화역")
                .jsonPath().getObject("", StationResponse.class);
        StationResponse secondLineDownStation = createStationRequest("오금역")
                .jsonPath().getObject("", StationResponse.class);

        firstLine = LineRequest.of("신분당선", "bg-red-600", firstLineUpStation.getId(), firstLineDownStation.getId(), 10);
        secondLine = LineRequest.of("3호선", "bg-bisque", secondLineUpStation.getId(), secondLineDownStation.getId(), 20);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성하면 조회 시 해당 노선을 찾을 수 있다.")
    @Test
    void createLine() {
        // when
        LineResponse line = createLineRequest(firstLine).jsonPath().getObject("", LineResponse.class);

        // then
        List<LineResponse> allLines = findAllLinesRequest().jsonPath().getList("", LineResponse.class);
        assertThat(allLines)
                .isNotEmpty()
                .hasSize(1)
                .contains(line);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("생성된 전체 지하철 노선 목록을 조회할 수 있다.")
    @Test
    void getLines() {
        // given
        LineResponse firstLineResponse = createLineRequest(firstLine).jsonPath().getObject("", LineResponse.class);
        LineResponse secondLineResponse = createLineRequest(secondLine).jsonPath().getObject("", LineResponse.class);

        // when
        List<LineResponse> allLines = findAllLinesRequest().jsonPath().getList("", LineResponse.class);

        // then
        assertThat(allLines)
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(firstLineResponse, secondLineResponse);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("생성된 특정 지하철 노선을 조회할 수 있다.")
    @Test
    void getLine() {
        // when
        ExtractableResponse<Response> response = createLineRequest(firstLine);
        int lineId = response.jsonPath().get("id");

        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        StationResponse upStation = stations.get(0);
        StationResponse downStation = stations.get(1);

        // when
        LineResponse lineResponse = findLineRequest(lineId).jsonPath().getObject("", LineResponse.class);

        // then
        assertThat(lineResponse)
                .satisfies(line -> {
                    assertThat(line.getName())
                            .isEqualTo(firstLine.getName());
                    assertThat(line.getColor())
                            .isEqualTo(firstLine.getColor());
                    assertThat(line.getStations())
                            .containsExactly(upStation, downStation);
                });
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 정보를 수정할 수 있다.")
    @Test
    void updateLine() {
        // given
        int lineId = createLineRequest(firstLine).jsonPath().get("id");

        // when
        updateLineRequest(lineId, secondLine);

        // then
        LineResponse lineResponse = findLineRequest(lineId).jsonPath().getObject("", LineResponse.class);
        assertThat(lineResponse)
                .satisfies(line -> {
                   assertThat(line.getName())
                           .isEqualTo(secondLine.getName());
                    assertThat(line.getColor())
                            .isEqualTo(secondLine.getColor());
                });
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 조회되지 않는다
     */
    @DisplayName("지하철 노선 정보를 삭제할 수 있다.")
    @Test
    void deleteLine() {
        // given
        int lineId = createLineRequest(firstLine).jsonPath().get("id");

        // when
        deleteLineRequest(lineId);

        // then
        ExtractableResponse<Response> lineResponse = findLineRequest(lineId);
        assertThat(lineResponse.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
