package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.exception.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceFixture.*;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성후_ID_를_리턴한다;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    public void setUp() throws SQLException {
        super.setUp();
        upStationId = 지하철역_생성후_ID_를_리턴한다("강남역");
        downStationId = 지하철역_생성후_ID_를_리턴한다("역삼역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("노선을 생성한다")
    void createLine() {
        // when
        노선을_생성한다("1호선", "RED", upStationId, downStationId, 10);

        // then
        List<String> name = 모든_노선을_조회한다("name");
        노선의_이름이_조회된다(name, "1호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("노선 목록을 조회한다")
    void showLine() {
        // given
        ExtractableResponse<Response> responseA = 노선을_생성한다("1호선", "RED", upStationId, downStationId, 10);
        ExtractableResponse<Response> responseB = 노선을_생성한다("2호선", "BLUE", upStationId, downStationId, 10);

        // when
        List<String> allLines = 모든_노선을_조회한다("name");

        // then
        상태코드를_체크한다(responseA.statusCode(), HttpStatus.CREATED.value());
        상태코드를_체크한다(responseB.statusCode(), HttpStatus.CREATED.value());
        노선의_이름이_조회된다(allLines, "1호선");
        노선의_이름이_조회된다(allLines, "2호선");
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("노선을 조회한다")
    void detailLine() {
        // given
        long lineId = 노선을_생성후_ID_를_리턴한다("1호선", "RED", upStationId, downStationId, 10);

        // when
        String name = 노선을_조회한후_이름을_리턴한다(lineId);

        // then
        노선의_이름이_일치하는지_확인한다(name, "1호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선을 수정한다")
    void updateLine() {
        // given
        long lineId = 노선을_생성후_ID_를_리턴한다("1호선", "RED", upStationId, downStationId, 10);

        // when
        ExtractableResponse<Response> response = 노선을_수정한다(lineId, "2호선", "BLUE");

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.OK.value());
        노선이_올바르게_수정되었는지_체크한다(노선을_조회한다(lineId), "2호선", "BLUE");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다")
    void deleteLine() {
        // given
        long lineId = 노선을_생성후_ID_를_리턴한다("1호선", "RED", upStationId, downStationId, 10);

        // when
        ExtractableResponse<Response> response = 노선을_삭제한다(lineId);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.NO_CONTENT.value());
        노선이_삭제되었는지_체크한다(모든_노선을_조회한다("name"), "1호선");
    }
}
