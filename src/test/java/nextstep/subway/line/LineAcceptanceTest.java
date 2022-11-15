package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationAcceptanceTestUtil;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceTestUtil.createLine;
import static nextstep.subway.line.LineAcceptanceTestUtil.getLines;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    String upStationId;
    String downStationId;
    String newDownStationId;


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        upStationId = StationAcceptanceTestUtil.createStation("신사역")
                .jsonPath()
                .getString("id");
        downStationId = StationAcceptanceTestUtil.createStation("광교(경기대)역")
                .jsonPath()
                .getString("id");
        newDownStationId = StationAcceptanceTestUtil.createStation("이매역")
                .jsonPath()
                .getString("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void 지하철노선생성_성공() {
        // when
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", upStationId, downStationId, "10");
        String lineName = response.jsonPath().getString("name");
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);

        // then
        assertAll(
                () -> assertThat(lineName).isEqualTo("신분당선"),
                () -> assertThat(stationNames).containsExactly("신사역", "광교(경기대)역")
        );
    }

    /**
     * Given 노선을 생성하고
     * When 기존에 존재하는 노선 이름으로 노선을 생성하면
     * Then 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void 지하철노선생성_실패_노선이름중복() {
        // when

        createLine("신분당선", "bg-red-600", upStationId, downStationId, "10");
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", upStationId, downStationId, "10");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철노선목록조회_성공() {
        // given
        createLine("신분당선", "bg-red-600", upStationId, downStationId, "200");
        createLine("경강선", "blue darken-2", upStationId, newDownStationId, "300");

        // when
        List<String> lineNames = getLines().jsonPath().getList("name", String.class);

        // then
        assertAll(
                () -> assertThat(lineNames).containsAnyOf("신분당선"),
                () -> assertThat(lineNames).containsAnyOf("경강선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 1개를 조회한다.")
    @Test
    void 지하철노선조회_성공() {
        // given
        Long id = createLine("신분당선", "bg-red-600", upStationId, downStationId, "200")
                .jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = getLines(id);
        Long lineId = response.jsonPath().getLong("id");
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);

        // then
        assertAll(
                () -> assertThat(lineId).isEqualTo(id),
                () -> assertThat(stationNames).containsExactly("신사역", "광교(경기대)역")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정하여 조회하면 수정된 노선 정보를 확인한다.")
    @Test
    void 지하철노선수정_성공() {
        // given
        Long id = createLine("신분당선", "bg-red-600", upStationId, downStationId, "200")
                .jsonPath()
                .getLong("id");

        // when
        String newName = "다른신분당선";
        String newColor = "blue darken-2";
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.updateLine(newName, newColor, id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
