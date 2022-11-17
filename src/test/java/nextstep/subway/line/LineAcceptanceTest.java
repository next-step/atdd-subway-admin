package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        int distance = 10;
        String expectLine = "3호선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("불광역")
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response =
                지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철_노선_전체_조회()
                .jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(expectLine);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        int distance = 10;
        String expectLine1 = "3호선";
        String expectLine2 = "분당선";

        Long upStationId = StationAcceptanceTest.지하철_역_생성("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("불광역")
                .jsonPath().getLong("id");

        지하철_노선_생성(expectLine1, "주황색", upStationId, downStationId, distance);
        지하철_노선_생성(expectLine2, "노랑색", upStationId, downStationId, distance);

        // when
        List<String> lineNames = 지하철_노선_전체_조회()
                .jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).contains(expectLine1, expectLine2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        // given
        int distance = 10;
        String expectLine = "3호선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("불광역")
                .jsonPath().getLong("id");

        Long lineId = 지하철_노선_생성(expectLine, "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");

        // when
        String result = 지하철_노선_조회(lineId)
                .jsonPath().getString("name");

        // then
        assertThat(result).isEqualTo(expectLine);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 노선에 포함된 역 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회하면, 노선에 포함된 지하철 역도 조회된다.")
    @Test
    void showStations() {
        // given
        int distance = 10;
        String stationName1 = "연신내역";
        String stationName2 = "불광역";

        Long upStationId = StationAcceptanceTest.지하철_역_생성("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("불광역")
                .jsonPath().getLong("id");

        Long lineId = 지하철_노선_생성("3호선", "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");

        // when
        List<String> stationNames = 지하철_노선_조회(lineId)
                .jsonPath().getList("stations.name", String.class);

        // then
        assertThat(stationNames).contains(stationName1, stationName2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        int distance = 10;
        String expectLineName = "2호선";
        Long upStationId = StationAcceptanceTest.지하철_역_생성("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("불광역")
                .jsonPath().getLong("id");

        Long lineId = 지하철_노선_생성("3호선", "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");

        // when
        지하철_노선_수정(lineId, expectLineName, "주황색");

        // then
        String result = 지하철_노선_조회(lineId)
                .jsonPath().getString("name");
        assertThat(result).isEqualTo(expectLineName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        int distance = 10;
        Long upStationId = StationAcceptanceTest.지하철_역_생성("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.지하철_역_생성("불광역")
                .jsonPath().getLong("id");
        Long lineId = 지하철_노선_생성("3호선", "주황색", upStationId, downStationId, distance)
                .jsonPath().getLong("id");

        // when
        지하철_노선_삭제(lineId);

        // then
        List<String> lineIds = 지하철_노선_전체_조회()
                .jsonPath().getList("id", String.class);
        assertThat(lineIds).isEmpty();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(
            String name,
            String color,
            Long upStationId,
            Long downStationId,
            int distance
    ) {
        LineRequest request = new LineRequest.Builder()
                .name(name)
                .color(color)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static Long 생성된_지하철_노선_ID_조회(
            String name,
            String color,
            Long upStationId,
            Long downStationId,
            int distance
    ) {
        return 지하철_노선_생성(name, color, upStationId, downStationId, distance).jsonPath()
                .getLong("id");
    }

    private ExtractableResponse<Response> 지하철_노선_수정(Long id, String name, String color) {
        LineUpdateRequest request = new LineUpdateRequest(name, color);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_전체_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
