package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.util.DataBaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static nextstep.subway.section.acceptance.SectionAcceptanceRequest.지하철구간_삭제_요청;
import static nextstep.subway.section.acceptance.SectionAcceptanceRequest.지하철구간_생성_요청;
import static nextstep.subway.section.acceptance.SectionAcceptanceRequest.지하철구간_존재;
import static nextstep.subway.section.acceptance.SectionAcceptanceRequest.지하철역과_노선_존재;
import static nextstep.subway.section.acceptance.SectionAcceptanceResponse.지하철구간_마지막_삭제_실패;
import static nextstep.subway.section.acceptance.SectionAcceptanceResponse.지하철구간_삭제_성공;
import static nextstep.subway.section.acceptance.SectionAcceptanceResponse.지하철구간_생성_성공;
import static nextstep.subway.section.acceptance.SectionAcceptanceResponse.지하철구간_생성_실패;
import static nextstep.subway.section.acceptance.SectionAcceptanceResponse.지하철구간_존재_삭제_실패;

@DisplayName("지하철구간 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DataBaseCleanUp dataBaseCleanUp;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            dataBaseCleanUp.afterPropertiesSet();
        }
        dataBaseCleanUp.execute();
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
     * WHEN 역 사이에 새로운 역을 등록하면
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
     * WHEN 새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면
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
     * WHEN 새로운 역을 상행 종점으로 생성하면
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
     * WHEN 새로운 역을 하행 종점으로 생성하면
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
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WHEN 구간을 삭제하면
     * THEN 존재하는 구간이 삭제된다
     */
    @DisplayName("구간을 삭제한다")
    @Test
    void 구간_삭제_성공() {
        지하철역과_노선_존재();
        String upStationId = "1";
        String downStationId = "3";
        int distance = 5;
        지하철구간_존재(upStationId, downStationId, distance);
        String targetStationId = "3";

        ExtractableResponse<Response> response = 지하철구간_삭제_요청(upStationId, downStationId, distance, targetStationId);

        지하철구간_삭제_성공(response);
    }

    /**
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WHEN 노선에 존재하지 않는 역이 포함된 구간을 삭제하려고 하면
     * THEN 예외를 던진다
     */
    @DisplayName("노선에 등록되지 않은 역을 구간에서 삭제하면 예외를 던진다")
    @Test
    void 구간_삭제_존재_예외() {
        지하철역과_노선_존재();
        String upStationId = "1";
        String downStationId = "3";
        int distance = 5;
        지하철구간_존재(upStationId, downStationId, distance);
        String targetStationId = "1000";

        ExtractableResponse<Response> response = 지하철구간_삭제_요청(upStationId, downStationId, distance, targetStationId);

        지하철구간_존재_삭제_실패(response);
    }

    /**
     * GIVEN 지하철 역과
     * GIVEN 1개의 노선과
     * GIVEN 1개의 구간이 존재 할 때
     * WHEN 마지막 구간을 삭제하려고 하면
     * THEN 예외를 던진다
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 삭제하면 예외를 던진다")
    @Test
    void 구간_삭제_마지막_예외() {
        지하철역과_노선_존재();
        String upStationId = "1";
        String downStationId = "2";
        int distance = 5;
        String targetStationId = "2";

        ExtractableResponse<Response> response = 지하철구간_삭제_요청(upStationId, downStationId, distance, targetStationId);

        지하철구간_마지막_삭제_실패(response);
    }
}
