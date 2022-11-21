package nextstep.subway.lineStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;

import static nextstep.subway.line.LineAcceptanceFixture.노선을_생성후_ID_를_리턴한다;
import static nextstep.subway.lineStation.LineStationAcceptanceFixture.*;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성후_ID_를_리턴한다;

@DisplayName("지하철구간 관련 기능")
public class LineStationAcceptanceTest extends AcceptanceTest {

    private Long upStationId;

    private Long downStationId;

    private Long lineId;

    private Long newStationId;

    @BeforeEach
    public void setUp() throws SQLException {
        super.setUp();
        upStationId = 지하철역_생성후_ID_를_리턴한다("강남역");
        downStationId = 지하철역_생성후_ID_를_리턴한다("역삼역");
        newStationId = 지하철역_생성후_ID_를_리턴한다("선릉역");

        lineId = 노선을_생성후_ID_를_리턴한다("1호선", "RED", upStationId, downStationId, 10);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 지하철구간을 추가하면
     * Then 노선에 새로운 지하철 역이 상행종점에 등록 된다.
     */
    @Test
    @DisplayName("새로운역을 상행 종점에 등록한다")
    void addUpLineStation() {
        // when
        ExtractableResponse<Response> response = 지하철_구간을_추가한다(lineId, newStationId, downStationId, 5);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.CREATED.value());
        지하철_구간이_추가되었는지_체크한다(response, "선릉역", "역삼역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철구간을 추가하면
     * Then 노선에 새로운 지하철 역이 하행종점에 등록 된다.
     */
    @Test
    @DisplayName("새로운역을 하행 종점에 등록한다")
    void addDownLineStation() {
        // when
        ExtractableResponse<Response> response = 지하철_구간을_추가한다(lineId, upStationId, newStationId, 5);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.CREATED.value());
        지하철_구간이_추가되었는지_체크한다(response, "강남역", "선릉역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철구간을 추가할때
     * Then 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
     */
    @Test
    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void sizeMinAndSameNotAddLineStationA() {
        // when
        ExtractableResponse<Response> response = 지하철_구간을_추가한다(lineId, upStationId, newStationId, 10);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철구간을 추가할때
     * Then 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
     */
    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void notContainStationError() {
        // given
        long newStationA = 지하철역_생성후_ID_를_리턴한다("수원역");
        long newStationB = 지하철역_생성후_ID_를_리턴한다("화서역");

        // when
        ExtractableResponse<Response> response = 지하철_구간을_추가한다(lineId, newStationA, newStationB, 5);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철구간을 추가할때
     * Then 상행역과 하행역 둘 다 모두 포함되어있으면 추가 할 수 없다.
     */
    @Test
    @DisplayName("상행역과 하행역 둘 다 모두 포함되어있으면 추가 할 수 없다.")
    void bothContainStationError() {
        // when
        ExtractableResponse<Response> response = 지하철_구간을_추가한다(lineId, upStationId, downStationId, 5);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.BAD_REQUEST.value());
    }
}
