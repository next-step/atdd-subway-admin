package nextstep.subway.linebridge;

import static nextstep.subway.linebridge.acceptance.LineBridgeAcceptanceRequest.*;
import static nextstep.subway.linebridge.acceptance.LineBridgeAcceptanceResponse.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Isolationer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;


@DisplayName("지하철구간 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineBridgeAcceptaneTest {

    @LocalServerPort
    int port;

    @Autowired
    private Isolationer dbIsolation;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dbIsolation.excute();
    }

    /**
     * GIVEN 지하철역이 존재하고
     * GIVEN 1개의 노선이 존재할 때
     * WHEN 지하철 구간을 생성하면
     * THEN 생성된 지하철 구간을 확인할 수 있다
     */
    @DisplayName("지하철 구간을 생성한다")
    @Test
    void 구간_생성_성공() {
        지하철역과_노선_존재();
        String upStationId = "1";
        String downStationId = "3";
        int distance = 5;

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_성공(response);
    }

    /**
     * GIVEN 지하철역이 존재하고
     * GIVEN 1개의 노선이 존재할 때
     * WHEN 동일한 구간을 생성하면
     * THEN 예외를 던진다
     */
    @DisplayName("지하철 구간이 중복된다면 예외를 던진다")
    @Test
    void 구간_생성_중복_예외() {
        String upStationId = "1";
        String downStationId = "3";
        int distance = 10;

        지하철역과_노선_존재();
        지하철구간_존재(downStationId, upStationId, distance);

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_실패(response);
    }

    /**
     * GIVEN 지하철역이 존재하고
     * GIVEN 1개의 노선이 존재할 때
     * WHEN 지하철 역 중 하나도 포함되어있지 않으면
     * THEN 예외를 던진다
     */
    @DisplayName("지하철 역 중 하나도 포함되어있지 않으면 예외를 던진다")
    @Test
    void 구간_생성_포함_예외() {
        지하철역과_노선_존재();
        String upStationId = "4";
        String downStationId = "5";
        int distance = 10;

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_실패(response);
    }

    /**
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WEHN 역 사이에 새로운 역을 등록하면
     * THEN 생성된 지하철 구간을 확인할 수 있다
     */
    @DisplayName("역 사이에 새로운 역 생성한다")
    @Test
    void 역_사이에_새로운_역_생성_성공() {
        지하철역과_노선_존재();
        String upStationId = "1";
        String downStationId = "3";
        int distance = 5;

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_성공(response);
    }

    /**
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WEHN 새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면
     * THEN 예외를 던진다
     */
    @DisplayName("역 사이에 새로운 역 생성한다")
    @Test
    void 역_사이에_새로운_역_생성_예외() {
        지하철역과_노선_존재();
        String upStationId = "1";
        String downStationId = "3";
        int distance = 10;

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_실패(response);
    }

    /**
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WEHN 새로운 역을 상행 종점으로 생성하면
     * THEN 생성된 지하철 구간을 확인할 수 있다
     */
    @DisplayName("새로운 역을 상행 종점으로 생성한다")
    @Test
    void 새로운_역_상행_종점_생성_성공() {
        지하철역과_노선_존재();
        String upStationId = "3";
        String downStationId = "1";
        int distance = 5;

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_성공(response);
    }

    /**
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WEHN 새로운 역을 하행 종점으로 생성하면
     * THEN 생성된 지하철 구간을 확인할 수 있다
     */
    @DisplayName("새로운 역을 하행 종점으로 생성한다")
    @Test
    void 새로운_역_하행_종점_생성_성공() {
        지하철역과_노선_존재();
        String upStationId = "2";
        String downStationId = "3";
        int distance = 5;

        ExtractableResponse<Response> response = 지하철구간_생성_요청(upStationId, downStationId, distance);

        지하철구간_생성_성공(response);
    }

    /**
     * GIVEN 2개의 구간을 생성하고
     * WHEN 구간의 종점을 삭제하면
     * THEN 지하철 노선에 종점이 삭제된다.
     */
    @Test
    void 구간의_종점을_삭제한다() {
        // given
        long lindId = 1L;
        long endStaionId =  1L;
        두개의_구간을_생성();

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(lindId, endStaionId);

        // then
        구간이_삭제된다(구간_삭제_응답);
    }

    /**
     * GIVEN 2개의 구간을 생성하고
     * WHEN 구간의 중간역을 삭제하면
     * THEN 지하철 노선에 종점이 삭제된다.
     */
    @Test
    void 구간의_중간역을_삭제한다() {
        // given
        long lindId = 1L;
        long middleStationId =  2L;
        두개의_구간을_생성();

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(lindId, middleStationId);

        // then
        구간이_삭제된다(구간_삭제_응답);
    }

    /**
     * GIVEN 새로운 역을 생성하고
     * WHEN 노선의 등록되지 않은 새로운 역을 삭제하면
     * THEN 구간이 삭제되지 않는다.
     */
    @Test
    void 노선에_등록되지_않은_역을_삭제한다() {
        // given
        long lindId = 1L;
        long notRegisteredStaionId =  5L;

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(lindId, notRegisteredStaionId);

        // then
        구간이_삭제되지_않는다(구간_삭제_응답);
    }

    /**
     * WHEN 구간이 하나인 노선의 마지막_역을_삭제하면
     * THEN 구간이 삭제되지 않는다.
     */
    @Test
    void 구간이_하나인_노선의_마지막_역을_삭제한다() {

        지하철역과_노선_존재();

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(1L, 2L);

        // then
        구간이_삭제되지_않는다(구간_삭제_응답);
    }
}
