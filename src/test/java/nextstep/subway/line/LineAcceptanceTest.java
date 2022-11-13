package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.AcceptanceTestFixture.응답코드가_일치한다;
import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.station.StationAcceptanceTestFixture.지하철역_생성;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철역_생성("강남역").jsonPath().getLong("id");
        downStationId = 지하철역_생성("판교역").jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // then
        응답코드가_일치한다(지하철_노선_생성_응답.statusCode(), HttpStatus.CREATED);
        지하철_노선_목록에_생성한_노선이_포함되어_있다(지하철_노선_이름_전체_목록(), "신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("2개의 지하철 노선을 생성하고 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성("2호선", "bg-red-600", upStationId, downStationId, 15);
        지하철_노선_생성("9호선", "bg-green-600", upStationId, downStationId, 10);

        // when
        List<String> 지하철_노선_이름_전체_목록 = 지하철_노선_이름_전체_목록();

        // then
        지하철_노선_목록에_생성한_노선이_포함되어_있다(지하철_노선_이름_전체_목록, "2호선", "9호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 생성하고 지하철 노선 목록을 조회한다.")
    @Test
    void createLineAndGetLines() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성 = 지하철_노선_생성("2호선", "bg-red-600", upStationId, downStationId, 15);

        // when
        ExtractableResponse<Response> 지하철_노선_조회 = 지하철_노선_조회(지하철_노선_생성.jsonPath().getLong("id"));

        // then
        지하철_노선_이름을_검증한다(지하철_노선_조회.jsonPath().getString("name"), "2호선");
    }
}
